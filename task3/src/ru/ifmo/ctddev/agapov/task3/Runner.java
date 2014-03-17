package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import ru.ifmo.ctddev.agapov.task3.generator.GClass;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 * Utility class, provides flexible and powerful interface to {@link Implementor}
 * <p/>
 * Usage:
 * <p/>
 * Syntax: java -jar [-debug] [-cp classPath] [-jar|-dir] class1, class2, .. [jarFile|outDir]
 * <p/>
 * Classpath format is just the same as for java utilite, look at it's '-cp' parameter
 * <p/>
 * To build jar file from classes' implementations:
 * <p/>
 * java -jar task3.jar -jar class1, class2, ... jarFile
 * <p/>
 * To put generated classes' implementations into outDir:
 * <p/>
 * java -jar task3.jar -dir class1, class2, ... outDir
 * <p/>
 * To put generated classes' implementations into current dir:
 * <p/>
 * java -jar task3.jar class1, class2, ...
 * <p/>
 * Hint: instead of class you can pass path to file with line-by-line list of classes.
 * To do so, pass @filePath as parameter in list of classes
 */
public class Runner {
    /**
     * Command line arguments
     */
    private String[] args;
    /**
     * Class names to process
     */
    private String[] classNames;

    /**
     * directory, to which we save generated implentations (code)
     */
    private File outDir = null;
    /**
     * directory, to which we put compiled files of implementation
     */
    private File buildDir = null;
    /**
     * file, to which we should save jar archive with built classes
     */
    private File jarFile = null;

    /**
     * true, if [-jar] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    boolean jarMode = false;
    /**
     * true, if [-dirMode] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    boolean dirMode = false;
    /**
     * true, if [-debug] argument was passed
     * Look at usage, described in {@link Runner} class description
     */
    boolean debugMode = false;

    /**
     * classPath, which we should use for processing files
     */
    String classPath = null;

    /**
     * GClass instances of classes, being implemented
     */
    GClass[] gClasses;

    /**
     * Constructs new Runner
     *
     * @param args command arguments
     */
    public Runner(String[] args) {
        this.args = args;
    }

    /**
     * Reads arguments, initializes process parameters
     *
     * @throws InvalidUsageException if inappropriate amount of arguments passed
     * @throws IOException           if error occurs, while reading class list file
     */
    private void init() throws InvalidUsageException, IOException {
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

    /**
     * Loads and implements classes
     *
     * @throws ImplerException        if it's impossible to implement some class
     * @throws ClassNotFoundException is some class couldn't be loaded (see {@link ru.ifmo.ctddev.agapov.task3.Implementor#implementClasses(Class[], java.io.File)})
     */
    private void implementClasses() throws ImplerException, ClassNotFoundException {
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

    /**
     * Creates output dir if needed
     *
     * @throws IOException if error occurs while trying to create temporary directory
     */
    private void createOutDir() throws IOException {
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

    /**
     * Compiles classes to {@link #buildDir}
     *
     * @throws CompilerException if compiler fails to compile generated code
     */
    private void compileClasses() throws CompilerException {
        ArrayList<String> files = new ArrayList<String>(gClasses.length);
        for (GClass gClass : gClasses) files.add(gClass.getOutputFile(outDir).getPath());
        int exitCode = runCompiler(outDir, files, buildDir, classPath);
        if (exitCode != 0) throw new CompilerException("Compiler finished with exitCode " + exitCode);
    }

    /**
     * Executes compiler
     *
     * @param srcDir    dir with sources(will be added to classpath)
     * @param files     files to compile
     * @param outDir    where to put built classes
     * @param classPath classpath parameter, null if none
     * @return compiler's exit code
     */
    private int runCompiler(File srcDir, List<String> files, File outDir, String classPath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> args = new ArrayList<String>();
        args.addAll(files);
        args.add("-cp");
        if (classPath != null && !classPath.isEmpty())
            classPath = classPath.concat(System.getProperty("path.separator")).concat(srcDir.getPath());
        else
            classPath = srcDir.getPath();
        args.add(classPath);
        args.add("-d");
        args.add(outDir.getPath());
        int exitCode = compiler.run(null, null, null, args.toArray(new String[args.size()]));
        return exitCode;
    }

    /**
     * Builds JAR file from built classes
     * @throws IOException if some IO exception occurs while creating JAR archive
     */
    private void buildJar() throws IOException {
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

    /**
     * Executes the utility
     */
    public void run() {
        try {
            init();
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

    /**
     * Loads classPath from {@link #classPath}
     * @throws IOException
     */
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

    /**
     * Prints usage message
     */
    public void printUsage() {
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

    /**
     * Deletes temporary directories
     * @throws IOException if some IO exception occurs while removing dirs
     */
    private void deleteTemporaryFolders() throws IOException {
        try {
            rmDir(outDir);
            rmDir(buildDir);
        } catch (IOException e) {
            throw new IOException("Can't delete temp folders", e);
        }
    }

    /**
     * Creates directory to put built classes into
     * @throws IOException
     */
    private void createBuildDir() throws IOException {
        try {
            buildDir = mkTmpDir();
        } catch (IOException e) {
            throw new IOException("Can't create temporary directory for class files", e);
        }
    }

    /**
     * Creates temporary directory
     * @return temporary directory File object
     * @throws IOException if some IO exception occurs while creating temp dir
     */
    private static File mkTmpDir() throws IOException {
        File file = File.createTempFile("implementor_tmp_", "");
        file.delete();
        file.mkdir();
        return file;
    }

    /**
     * Recursively removes directory
     * @param dir directory to remove
     * @throws IOException
     */
    protected static void rmDir(File dir) throws IOException {
        Files.walkFileTree(Paths.get(dir.getPath()), new SimpleFileVisitor<Path>() {
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

    /**
     * Is being thrown, when invalid set of parameters is passed
     */
    protected static class InvalidUsageException extends Exception {

    }

    /**
     * Is being thrown when compiler fails to compile generated implementations' files
     */
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
