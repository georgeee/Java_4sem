package ru.ifmo.ctddev.agapov.task3;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;


/**
 * Created by georgeee on 16.03.14.
 */
public class Main {
    public static void main(String [] args) throws ImplerException {
        Class[] classes = new Class[]{
                javax.management.remote.rmi.RMIServerImpl.class, javax.naming.ldap.LdapReferralException.class,
                javax.annotation.processing.Completions.class,
                java.util.Iterator.class,
                java.util.NavigableSet.class, java.util.NavigableMap.class,
                javax.imageio.stream.ImageOutputStream.class, java.io.InputStreamReader.class,
                java.io.Reader.class, java.io.InputStream.class,
                ru.ifmo.ctddev.agapov.task3.test.template.ExtendedSTInterface.class,
                ru.ifmo.ctddev.agapov.task3.test.template.DefaultPair.class,
                ru.ifmo.ctddev.agapov.task3.test.template.DefaultPair2.class,
                ru.ifmo.ctddev.agapov.task3.test.template.DefaultPair3.class,
                ru.ifmo.ctddev.agapov.task3.test.AbstractNavigableMap.class,
                ru.ifmo.ctddev.agapov.task3.test.template.MyMap.class,
                ru.ifmo.ctddev.agapov.task3.test.template.Pair.class,
                ru.ifmo.ctddev.agapov.task3.test.template.DefaultPair.class,
                ru.ifmo.ctddev.agapov.task3.test.template.SimpleTemplateInterface.class,
                ru.ifmo.ctddev.agapov.task3.test.BottomAbstractClass.class,
                ru.ifmo.ctddev.agapov.task3.test.FullyImplementedClass.class,
                ru.ifmo.ctddev.agapov.task3.test.MiddleAbstractClass.class,
                ru.ifmo.ctddev.agapov.task3.test.SimpleAbstractClass.class,
                ru.ifmo.ctddev.agapov.task3.test.SimpleInterface.class,
        };
        new Implementor().implementClasses(classes, new File(System.getProperty("user.dir")));
    }

}
