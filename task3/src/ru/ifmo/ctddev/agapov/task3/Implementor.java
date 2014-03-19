package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import ru.ifmo.ctddev.agapov.task3.generator.GClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementor is a simple class, which provides you to implement some abstract class or interface
 */
public class Implementor extends BaseJarImpler {
    /**
     * Creates new Implementor instance
     */
    public Implementor() {
    }


    /**
     * Implements class, puts it's java code in path, relative to root
     *
     * @param clazz Token of class to implement
     * @param root  Root of directory where to put generated file
     * @throws ImplerException when it's impossible to implement class or IOException occurred
     */
    @Override
    public File implementWithFile(Class<?> clazz, File root) throws ImplerException {
        GClass gClass;
        PrintWriter pw = null;
        try {
            gClass = new GClass(clazz);
            String gClassString = gClass.toString();
            pw = new PrintWriter(gClass.getOutputFile(root));
            pw.print(gClassString);
            pw.close();
        } catch (IOException ex) {
            if (pw != null) pw.close();
            throw new ImplerException("IO exception occured while processing class " + clazz.getName(), ex);
        }
        return gClass.getOutputFile(root);
    }

    /**
     * Main function, where everything begins
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Runner(args).run();
    }

    @Override
    public void implementClassesJar(Class<?>[] classes, File jarFile, String classPath) throws ImplerException {
        File[] files = new File[classes.length];
        File implDir = null;
        try {
            try {
                implDir = Utility.mkTmpDir();
            } catch (IOException e) {
                throw new ImplerException("Can't create temporary directory for implementation (source) files", e);
            }
            for (int i = 0; i < classes.length; ++i) files[i] = implementWithFile(classes[i], implDir);
            JarCompiler jarCompiler = new JarCompiler(files, classPath);
            try {
                jarCompiler.compile(jarFile);
            } catch (IOException|JarCompiler.CompilerException e) {
                throw new ImplerException(e.getMessage(), e);
            }
        } finally {
            if (implDir != null && implDir.exists()) {
                try {
                    Utility.rmDir(implDir);
                } catch (IOException e) {
                }
            }
        }
    }
}
