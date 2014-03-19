package ru.ifmo.ctddev.agapov.task3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 * Helper class for Implementor
 */
public class Utility {
    /**
     * Loads classPath from {@link #classPath}
     * @throws java.io.IOException
     */
    /**
     * Loads classes, defined by classPath parameter, nothing if it's null
     *
     * @param classPath classPath in default system format (files|directories, separated by path.separator property)
     * @throws IOException if error occurs loading some part of classpath
     */
    public static void loadClassPath(String classPath) throws IOException {
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
     * Creates temporary directory
     *
     * @return temporary directory File object
     * @throws IOException if some IO exception occurs while creating temp dir
     */
    public static File mkTmpDir() throws IOException {
        File file = File.createTempFile("implementor_tmp_", "");
        file.delete();
        file.mkdir();
        return file;
    }

    /**
     * Recursively removes directory
     *
     * @param dir directory to remove
     * @throws IOException
     */
    public static void rmDir(File dir) throws IOException {
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
     * Loads Class tokens for array of class names
     *
     * @param classNames array of class names
     * @return array of class tokens for given class names
     * @throws ClassNotFoundException if some class couldn't be loaded
     */
    public static Class<?>[] loadClasses(String[] classNames) throws ClassNotFoundException {
        Class<?>[] classes = new Class[classNames.length];
        for (int i = 0; i < classes.length; ++i) {
            String className = classNames[i];
            try {
                classes[i] = ClassLoader.getSystemClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("Class not found: " + className, e);
            }
        }
        return classes;
    }

}
