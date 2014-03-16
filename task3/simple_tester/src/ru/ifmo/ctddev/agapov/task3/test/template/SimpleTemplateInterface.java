package ru.ifmo.ctddev.agapov.task3.test.template;

/**
 * Created by georgeee on 13.03.14.
 */
public interface SimpleTemplateInterface<T> {
    Pair<Pair<Pair<T,T>,T>,T> getTuple(T a, A.B.C cc, Pair<T, T> b, Pair<Pair<T,T>,T> c);
    <E> Pair<T, E> mkPair(E b);

    interface A{
        interface B{
            interface C{
                void abc();
            }
        }
    }

}
