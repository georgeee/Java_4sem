package ru.ifmo.ctddev.agapov.task3;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Jar Compiler - compiles given java sources to jar
 */
public class JarCompiler {
    /**
     * classPath, which we should pass to compiler utility
     */
    private String classPath;
    /**
     * source java files, that should be compiled to JAR
     */
    private File[] sourceFiles;

    /**
     * Creates instance by array of source files
     *
     * @param sourceFiles array of source files
     */
    public JarCompiler(File[] sourceFiles) {
        this(sourceFiles, null);
    }

    /**
     * Creates instance by array of source files and given classpath (parameter for compiler)
     *
     * @param sourceFiles array of source files
     * @param classPath   classPath string, standard (paths divided by path.separator property)
     */
    public JarCompiler(File[] sourceFiles, String classPath) {
        this.classPath = classPath;
        this.sourceFiles = sourceFiles;
    }

    /**
     * Returns classPath, which we should pass to compiler utility
     *
     * @return classPath string, standard (paths divided by path.separator property)
     */
    public String getClassPath() {
        return classPath;
    }

    /**
     * Sets classPath, which we should pass to compiler utility
     *
     * @param classPath string, standard (paths divided by path.separator property)
     */
    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    /**
     * Adds part of classPath to the end of classPath
     *
     * @param classPathPart paths, separated by path.separator property
     */
    public void addToClassPath(String classPathPart) {
        if (classPath != null && !classPath.isEmpty())
            classPath = classPath.concat(System.getProperty("path.separator")).concat(classPathPart);
        else
            classPath = classPathPart;
    }

    /**
     * Adds file  to the end of classPath
     *
     * @param file file to add to classPath
     */
    public void addToClassPath(File file) {
        addToClassPath(file.getPath());
    }

    /**
     * Compiles files to jarFile
     * @param jarFile resulting <tt>.jar</tt> file
     * @throws IOException if some IO error occurs
     * @throws CompilerException if compiler fails to compile code
     */
    public void compile(File jarFile) throws IOException, CompilerException {
        File buildDir = null;
        try {
            try {
                buildDir = Utility.mkTmpDir();
            } catch (IOException e) {
                throw new IOException("Can't create temporary directory for class files", e);
            }
            compileClasses(buildDir);
            buildJar(buildDir, jarFile);
        } finally {
            if (buildDir != null && buildDir.exists()) {
                try {
                    Utility.rmDir(buildDir);
                } catch (IOException e) {
                }
            }
        }
    }


    /**
     * Compiles classes
     *
     * @param buildDir File, to which we put built classes
     * @throws CompilerException if compiler fails to compile classes code
     */
    private void compileClasses(File buildDir) throws CompilerException {
        ArrayList<String> files = new ArrayList<String>(sourceFiles.length);
        for (File file : sourceFiles) files.add(file.getPath());
        int exitCode = runCompiler(files, buildDir);
        if (exitCode != 0) throw new CompilerException("Compiler finished with exitCode " + exitCode);
    }

    /**
     * Executes compiler
     *
     * @param files  files to compile
     * @param outDir where to put built classes
     * @return compiler's exit code
     */
    private int runCompiler(List<String> files, File outDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<String> args = new ArrayList<String>();
        args.addAll(files);
        if (classPath != null) {
            args.add("-cp");
            args.add(classPath);
        }
        args.add("-d");
        args.add(outDir.getPath());
        int exitCode = compiler.run(null, null, null, args.toArray(new String[args.size()]));
        return exitCode;
    }

    /**
     * Builds JAR file from built classes
     *
     * @throws java.io.IOException if some IO exception occurs while creating JAR archive
     */
    private void buildJar(File buildDir, File jarFile) throws IOException {
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
