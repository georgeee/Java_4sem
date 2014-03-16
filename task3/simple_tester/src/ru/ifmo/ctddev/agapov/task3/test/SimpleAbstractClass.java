package ru.ifmo.ctddev.agapov.task3.test;

/**
 * Created by georgeee on 13.03.14.
 */
public abstract class SimpleAbstractClass extends MiddleAbstractClass implements SimpleInterface {
    @Override
    public void a() {
        cf();
    }

    protected abstract void cf();

    public abstract void cg();

    @Override
    public void bcb(int v) {

    }
}
