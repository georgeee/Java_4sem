package ru.georgeee.itmo.java.sem4.common;

import javafx.util.Pair;

public class StringHashCodeTask implements Task<Pair<String, Integer>, String> {

    private long timeOut;

    public StringHashCodeTask(long timeOut) {
        this.timeOut = timeOut;
    }

    @Override
    public Pair<String, Integer> run(String value) {
        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
        }
        return new Pair<>(value, value.hashCode());
    }
}
