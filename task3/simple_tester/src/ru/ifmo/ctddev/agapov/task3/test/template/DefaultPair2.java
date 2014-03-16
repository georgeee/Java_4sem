package ru.ifmo.ctddev.agapov.task3.test.template;

import java.util.Set;

/**
 * Created by georgeee on 13.03.14.
 */
public abstract class DefaultPair2<F> extends DefaultPair<F,Set<Integer>> {

    public DefaultPair2(F f, Set<Integer> s) {
        super(f, s);
    }
}
