package ru.ifmo.ctddev.agapov.task3.test.template;

/**
 * Created by georgeee on 13.03.14.
 */
public interface Pair<F,S> {
    F getA();
    S getB();
    Pair<S,F> reverse();

    public abstract void aa(Class<? extends S>[] b);
}
