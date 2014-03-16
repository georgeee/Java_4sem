package ru.ifmo.ctddev.agapov.task3.test.template;

/**
 * Created by georgeee on 13.03.14.
 */
public abstract class DefaultPair3<F,S> extends DefaultPair<F, Object> {
    protected DefaultPair3(F f, Object o) {
        super(f, o);
    }

    public abstract void ttt(Class<?> ds);

    protected abstract void yahoo(Class<?> cl);

    abstract void abcdef(Class<?> cl);


}
