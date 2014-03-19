package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.io.File;

/**
 * Base class for Implementor, holds most synonyms and routines
 */
public abstract class BaseJarImpler implements JarImpler {
    /**
     * Produces code implementing class or interface specified by provided <tt>token</tt>.
     * <p/>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * <tt>root</tt> directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to <tt>$root/java/util/ListImpl.java</tt>
     *
     * @param token type token to create implementation for.
     * @param root  root directory.
     * @return file with generated java code of implementation
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     *                                                                 generated.
     */
    public abstract File implementWithFile(Class<?> token, File root) throws ImplerException;

    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        implementWithFile(token, root);
    }

    @Override
    public void implementJar(Class<?> token, File jarFile) throws ImplerException {
        implementJar(token, jarFile, null);
    }


    /**
     * Implements classes, puts generated java code in path, relative to root
     *
     * @param classes Array of class tokens to implement
     * @param root    Root of directory where to put generated files
     * @throws ImplerException when it's impossible to implement class or IOException occurred
     */
    public void implementClasses(Class<?>[] classes, File root) throws ImplerException {
        for (int i = 0; i < classes.length; ++i) implement(classes[i], root);
    }

    /**
     * Produces <tt>.jar</tt> file implementing class or interface specified by provided <tt>token</tt>.
     * <p/>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param token     type token to create implementation for.
     * @param jarFile   target <tt>.jar</tt> file.
     * @param classPath classPath string, null if not needed
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be generated.
     */
    public void implementJar(Class<?> token, File jarFile, String classPath) throws ImplerException {
        implementClassesJar(new Class<?>[]{token}, jarFile, classPath);
    }

    /**
     * Produces <tt>.jar</tt> file implementing classes or interfaces specified by provided <tt>token array</tt>.
     * <p/>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param classes type token array to create implementations for.
     * @param jarFile target <tt>.jar</tt> file.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be generated.
     */
    public void implementClassesJar(Class<?>[] classes, File jarFile) throws ImplerException {
        implementClassesJar(classes, jarFile, null);
    }

    /**
     * Produces <tt>.jar</tt> file implementing classes or interfaces specified by provided <tt>token array</tt>.
     * <p/>
     * Generated class full name should be same as full name of the type token with <tt>Impl</tt> suffix
     * added.
     *
     * @param classes   type token array to create implementations for.
     * @param jarFile   target <tt>.jar</tt> file.
     * @param classPath classPath string, null if not needed
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be generated.
     */
    public abstract void implementClassesJar(Class<?>[] classes, File jarFile, String classPath) throws ImplerException;

}
