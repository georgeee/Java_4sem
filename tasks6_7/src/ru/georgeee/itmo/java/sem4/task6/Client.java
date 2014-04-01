package ru.georgeee.itmo.java.sem4.task6;

import ru.georgeee.itmo.java.sem4.common.StringHashCodeTask;
import ru.georgeee.itmo.java.sem4.common.Utility;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerIsDownException;

public class Client implements Runnable {
    private volatile boolean isStopped = false;
    private volatile boolean isStarted = false;
    private TaskRunner taskRunner;
    private StringHashCodeTask task;

    public Client(TaskRunner taskRunner, long taskSleepTimeout) {
        this.taskRunner = taskRunner;
        this.task = new StringHashCodeTask(taskSleepTimeout);
    }

    public void stop() {
        isStopped = true;
    }

    public void start() {
        synchronized (this) {
            if (isStarted) throw new RuntimeException("Client has already been launched");
            isStarted = true;
        }
        System.out.println("Client " + toString() + " started");
        while (!isStopped) {
            String string = Utility.generateRandomString();
            Integer hashCode;
            try {
                hashCode = taskRunner.run(task, string).getValue();
            } catch (TaskRunnerIsDownException ex) {
                System.out.printf("Processing string %s to taskRunner had been interrupted by taskRunner's shut down\n", string);
                isStopped = true;
                break;
            }
            System.out.printf("%s.hashCode() = %d\n", string, hashCode);
        }
        System.out.println("Client " + toString() + " stopped");
    }

    @Override
    public void run() {
        start();
    }

}
