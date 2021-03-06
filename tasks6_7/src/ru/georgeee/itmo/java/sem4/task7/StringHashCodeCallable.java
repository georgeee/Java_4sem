package ru.georgeee.itmo.java.sem4.task7;

import javafx.util.Pair;
import ru.georgeee.itmo.java.sem4.task6.misc.StringHashCodeTask;
import ru.georgeee.itmo.java.sem4.task6.misc.Utility;

import java.util.concurrent.Callable;

/**
 * Created by georgeee on 02.04.14.
 */
public class StringHashCodeCallable implements Callable<Pair<String, Integer>> {
    StringHashCodeTask task;

    public StringHashCodeCallable(StringHashCodeTask task) {
        this.task = task;
    }

    @Override
    public Pair<String, Integer> call() throws Exception {
        return task.run(Utility.generateRandomString());
    }
}
