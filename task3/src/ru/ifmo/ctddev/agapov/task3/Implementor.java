package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by georgeee on 13.03.14.
 */
public class Implementor implements Impler {
    public String implementToString(Class<?> token) throws ImplerException {
        GClass gClass = new GClass(token);
        return gClass.toString();
    }

    public GClass implementWithGClass(Class<?> token, File root) throws ImplerException {
        GClass gClass;
        PrintWriter pw = null;
        try {
            gClass = new GClass(token);
            String gClassString = gClass.toString();
            pw = new PrintWriter(gClass.getOutputFile(root));
            pw.print(gClassString);
            pw.close();
        } catch (IOException ex) {
            if (pw != null) pw.close();
            throw new ImplerException("IO exception occured while processing class " + token.getName(), ex);
        }
        return gClass;
    }


    @Override
    public void implement(Class<?> token, File root) throws ImplerException {
        implementWithGClass(token, root);
    }


    public GClass[] implementClasses(Class<?>[] classes, File root) throws ImplerException {
        GClass [] gClasses = new GClass[classes.length];
        for(int i=0; i<classes.length; ++i) gClasses[i] = implementWithGClass(classes[i], root);
        return gClasses;
    }

    public static void main(String[] args) {
        new Runner(args).run();
    }

}
