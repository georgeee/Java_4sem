package ru.georgeee.itmo.java.sem4.common;

/**
 * Created by georgeee on 01.04.14.
 */
public interface Task<X, Y> {
    X run(Y value);
}