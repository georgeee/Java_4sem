package ru.georgeee.itmo.java.sem4.task7;

import javafx.util.Pair;
import ru.georgeee.itmo.java.sem4.common.CallableFactory;
import ru.georgeee.itmo.java.sem4.common.StringHashCodeCallable;
import ru.georgeee.itmo.java.sem4.common.StringHashCodeTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class Main {
    private static final int TASK_SLEEP_TIMEOUT = 1000;

    public static void main(String[] args) {
        Environment<Pair<String, Integer>> environment = new Environment<>(new CallableFactory<Pair<String, Integer>>() {
            Callable<Pair<String, Integer>> callable = new StringHashCodeCallable(new StringHashCodeTask(TASK_SLEEP_TIMEOUT));

            @Override
            public Callable<Pair<String, Integer>> generateCallable() {
                return callable;
            }
        }, 10, 10, 10);
        environment.run();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            in.readLine();
        } catch (IOException e) {
        }
        environment.shutdown();
    }
}
