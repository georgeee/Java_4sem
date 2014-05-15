package ru.georgeee.itmo.java.sem4.task6;

import ru.georgeee.itmo.java.sem4.task6.interfaces.ExtendedTaskRunner;
import ru.georgeee.itmo.java.sem4.task6.misc.Client;
import ru.georgeee.itmo.java.sem4.task6.misc.TaskRunnerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main implements Runnable {
    private static final int RUNNER_COUNT = 5;
    private static final int RUNNER_THREAD_POOL_SIZE = 2;
    private static final int CLIENT_COUNT = RUNNER_COUNT * RUNNER_THREAD_POOL_SIZE * 50;
    private static final int TASK_SLEEP_TIMEOUT = 1000;
    private Client[] clients;
    private ExtendedTaskRunner[] runners;

    public Main(int runnerCount, int clientCount) {
        runners = new ExtendedTaskRunner[runnerCount];
        for (int i = 0; i < runnerCount; ++i) {
            runners[i] = new TaskRunnerImpl(RUNNER_THREAD_POOL_SIZE);
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
        }finally {
            stop();
        }
    }
}
