package ru.georgeee.itmo.java.sem4.task7;

import java.util.concurrent.Callable;

/**
 * Created by georgeee on 02.04.14.
 */
public interface CallableFactory<V> {
    Callable<V> generateCallable();
}
