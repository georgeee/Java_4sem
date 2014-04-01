package ru.georgeee.itmo.java.sem4.common;

import java.util.concurrent.Callable;

/**
 * Created by georgeee on 02.04.14.
 */
public interface CallableFactory<V> {
    Callable<V> generateCallable();
}
