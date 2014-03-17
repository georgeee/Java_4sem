package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import ru.ifmo.ctddev.agapov.task3.generator.GClass;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Implementor is a simple class, which provides you to implement some abstract class or interface
 */
public class Implementor implements Impler {
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
     * @return GClass instance for clazz
     * @throws ImplerException when it's impossible to implement class or IOException occurred
     */
    public GClass implementWithGClass(Class<?> clazz, File root) throws ImplerException {
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
        return gClass;
    }


    /**
     * Implements class, puts it's java code in path, relative to root
     *
     * @param clazz Token of class to implement
     * @param root  Root of directory where to put generated file
     * @throws ImplerException when it's impossible to implement class or IOException occurred
     */
    @Override
    public void implement(Class<?> clazz, File root) throws ImplerException {
        implementWithGClass(clazz, root);
    }


    /**
     * Implements classes, puts generated java code in path, relative to root
     *
     * @param classes Array of class tokens to implement
     * @param root    Root of directory where to put generated files
     * @return GClass instances for classes
     * @throws ImplerException when it's impossible to implement class or IOException occurred
     */
    public GClass[] implementClasses(Class<?>[] classes, File root) throws ImplerException {
        GClass[] gClasses = new GClass[classes.length];
        for (int i = 0; i < classes.length; ++i) gClasses[i] = implementWithGClass(classes[i], root);
        return gClasses;
    }

    /**
     * Main function, where everything begins
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Runner(args).run();
    }

}
