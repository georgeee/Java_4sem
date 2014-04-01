package ru.georgeee.itmo.java.sem4.task6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by georgeee on 01.04.14.
 */
public class Main implements Runnable {
    public static final long FUTURE_GET_TIMEOUT_MILLIS = 400;
    public static final long AWAIT_TERMINATION_MILLIS = 5000;
    private static final int RUNNER_COUNT = 5;
    private static final int RUNNER_THREAD_POOL_SIZE = 10;
    private static final int CLIENT_COUNT = RUNNER_COUNT * RUNNER_THREAD_POOL_SIZE * 30;
    private static final int TASK_SLEEP_TIMEOUT = 1000;
    private Client[] clients;
    private ExtendedTaskRunner[] runners;

    public Main(int runnerCount, int clientCount) {
        runners = new ExtendedTaskRunner[runnerCount];
        for (int i = 0; i < runnerCount; ++i) {
            runners[i] = new TaskRunnerImpl(RUNNER_THREAD_POOL_SIZE, FUTURE_GET_TIMEOUT_MILLIS, AWAIT_TERMINATION_MILLIS);
        }
        clients = new Client[clientCount];
        for (int i = 0; i < clientCount; ++i) {
            clients[i] = new Client(runners[i % runnerCount], TASK_SLEEP_TIMEOUT);
        }
    }

    public static void main(String[] args) {
        Main main = new Main(RUNNER_COUNT, CLIENT_COUNT);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        main.run();
    }

    public void stop() {
        for (int i = 0; i < clients.length; ++i) {
            clients[i].stop();
        }
        for (int i = 0; i < runners.length; ++i) {
            runners[i].shutdown();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < clients.length; ++i) {
            new Thread(clients[i]).start();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            in.readLine();
        } catch (IOException e) {
        }
        stop();
    }
}
