package ru.georgeee.itmo.java.sem4.task6.misc;

import javafx.util.Pair;
import ru.georgeee.itmo.java.sem4.task6.interfaces.Task;

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
