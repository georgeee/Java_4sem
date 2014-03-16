package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 * Created by georgeee on 16.03.14.
 */
public class Runner {
    String[] args;
    String[] classNames;

    File outDir = null;
    File buildDir = null;
    File jarFile = null;

    boolean jarMode = false;
    boolean dirMode = false;
    boolean debugMode = false;

    String classPath = null;

    GClass[] gClasses;

    public Runner(String[] args) {
        this.args = args;
    }

    void init(String[] args) throws InvalidUsageException, IOException {
        if (args.length == 0) throw new InvalidUsageException();
        int i = 0;
        if (args[i].equals("-debug")) {
            debugMode = true;
            ++i;
        }
        if (args[i].equals("-cp")) {
            classPath = args[++i];
            ++i;
        }
        if (args[i].equals("-jar")) {
            ++i;
            jarMode = true;
        } else if (args[i].equals("-dir")) {
            ++i;
            dirMode = true;
        }
        int classCount = args.length - i - (jarMode || dirMode ? 1 : 0);
        if (classCount < 1) throw new InvalidUsageException();
        ArrayList<String> classNamesList = new ArrayList<>(classCount);

        for (int j = 0; j < classCount; ++j) {
            String arg = args[i + j];
            if (arg.charAt(0) == '@') {
                String filePath = arg.substring(1);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        classNamesList.add(line);
                    }
                } catch (FileNotFoundException e) {
                    throw new FileNotFoundException("File " + filePath + " from arguments was not found");
                } catch (IOException e) {
                    throw new IOException("Error occurred while reading file " + filePath + " (from arguments)", e);
                } finally {
                    if (br != null) br.close();
                }
            } else {
                classNamesList.add(arg);
            }
        }

        if (dirMode)
            outDir = new File(args[args.length - 1]);
        else if (!jarMode)
            outDir = new File(System.getProperty("user.dir"));

        if (jarMode)
            jarFile = new File(args[args.length - 1]);

        classNames = classNamesList.toArray(new String[classNamesList.size()]);
    }

    protected void implementClasses() throws ImplerException, ClassNotFoundException {
        Class<?>[] classes = new Class[classNames.length];
        for (int i = 0; i < classes.length; ++i) {
            String className = classNames[i];
            try {
                classes[i] = ClassLoader.getSystemClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("Class not found: " + className, e);
            }
        }
        gClasses = new Implementor().implementClasses(classes, outDir);
    }

    protected void createOutDir() throws IOException {
        if (jarMode) {
            try {
                outDir = mkTmpDir();
            } catch (IOException e) {
                throw new IOException("Can't create temporary directory for sources", e);
            }
        } else if (!outDir.exists()) {
            outDir.mkdirs();
        }
    }

    protected void compileClasses() throws CompilerException {
        ArrayList<String> files = new ArrayList<String>(gClasses.length);
        for (GClass gClass : gClasses) files.add(gClass.getOutputFile(outDir).getPath());
        int exitCode = Compiler.runCompiler(outDir, files, buildDir, classPath);
        if (exitCode != 0) throw new CompilerException("Compiler finished with exitCode " + exitCode);
    }

    protected void buildJar() throws IOException {
        JarOutputStream _jas = null;
        try {
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            _jas = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(jarFile)), manifest);
            final JarOutputStream jas = _jas;
            final Path buildDirPath = Paths.get(buildDir.getPath());
            try {
                Files.walkFileTree(buildDirPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                        File file = filePath.toFile();
                        String entryName = buildDirPath.relativize(filePath).toString();
                        JarEntry entry = new JarEntry(entryName);
                        entry.setTime(file.lastModified());
                        jas.putNextEntry(entry);
                        BufferedInputStream bis = null;
                        try {
                            bis = new BufferedInputStream(new FileInputStream(file));
                            byte[] buffer = new byte[65536];
                            while (true) {
                                int count = bis.read(buffer);
                                if (count == -1) break;
                                jas.write(buffer, 0, count);
                            }
                        } finally {
                            if (bis != null) bis.close();
                        }
                        jas.closeEntry();
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new IOException("IO exception caught while building jar", e);
            }
        } finally {
            if (_jas != null) _jas.close();
        }
    }

    public void run() {
        try {
            init(args);
            if (debugMode) System.err.println("==== Debug mode on ====");
            loadClassPath();
            createOutDir();
            implementClasses();
            if (jarMode) {
                createBuildDir();
                compileClasses();
                buildJar();
                deleteTemporaryFolders();
            }
        } catch (ImplerException | ClassNotFoundException | IOException | CompilerException e) {
            System.err.println(e.getMessage());
            if (debugMode) e.printStackTrace();
        } catch (InvalidUsageException e) {
            printUsage();
        }
    }

    private void loadClassPath() throws IOException {
        if (classPath != null) {
            String[] parts = classPath.split(Pattern.quote(System.getProperty("path.separator")));
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            for (int i = 0; i < parts.length; ++i) {
                try {
                    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
                    method.setAccessible(true);
                    method.invoke(sysloader, new Object[]{new File(parts[i]).toURI().toURL()});
                } catch (Exception ex) {
                    throw new IOException("Error while adding classpath, could not add " + parts[i] + " to system classloader: " + ex.getMessage(), ex);
                }
            }
        }
    }


    protected void printUsage() {
        System.out.println("Usage:\n" +
                "Syntax: java -jar [-debug] [-cp classPath] [-jar|-dir] class1, class2, .. [jarFile|outDir]\n" +
                "Classpath format is just the same as for java utilite, look at it's '-cp' parameter\n\n" +
                "To build jar file from classes' implementations: \n" +
                "   java -jar task3.jar -jar class1, class2, ... jarFile\n" +
                "To put generated classes' implementations into outDir: \n" +
                "   java -jar task3.jar -dir class1, class2, ... outDir\n" +
                "To put generated classes' implementations into current dir: \n" +
                "   java -jar task3.jar class1, class2, ...\n\n" +
                "Hint: instead of class you can pass path to file with line-by-line list of classes.\n" +
                "To do so, pass @filePath as parameter in list of classes\n");
    }

    protected void deleteTemporaryFolders() throws IOException {
        try {
            rmDir(outDir);
            rmDir(buildDir);
        } catch (IOException e) {
            throw new IOException("Can't delete temp folders", e);
        }
    }


    protected void createBuildDir() throws IOException {
        try {
            buildDir = mkTmpDir();
        } catch (IOException e) {
            throw new IOException("Can't create temporary directory for class files", e);
        }
    }

    protected static File mkTmpDir() throws IOException {
        File file = File.createTempFile("implementor_tmp_", "");
        file.delete();
        file.mkdir();
        return file;
    }

    protected static void rmDir(File currentDir) throws IOException {
        Files.walkFileTree(Paths.get(currentDir.getPath()), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                file.toFile().delete();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                dir.toFile().delete();
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    protected static class InvalidUsageException extends Exception {

    }


    protected static class CompilerException extends Exception {
        public CompilerException() {
        }

        public CompilerException(String message) {
            super(message);
        }

        public CompilerException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
