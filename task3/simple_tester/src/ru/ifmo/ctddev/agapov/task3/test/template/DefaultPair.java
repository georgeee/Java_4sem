package ru.ifmo.ctddev.agapov.task3.test.template;

/**
 * Created by georgeee on 13.03.14.
 */
public abstract class DefaultPair<A, B> implements Pair<A, B> {
    public DefaultPair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    A a;
    B b;

    @Override
    public A getA() {
        return a;
    }

    @Override
    public B getB() {
        return b;
    }


    public abstract void ab(Class<? extends A>[] b);
}
