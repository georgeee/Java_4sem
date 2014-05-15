package ru.georgeee.itmo.java.sem4.task6.misc;

import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerCancelledException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerImplResultWaitInterruptedException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerIsDownException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerRunException;
import ru.georgeee.itmo.java.sem4.task6.interfaces.TaskRunner;

public class Client implements Runnable {
    private volatile boolean isStopped = false;
    private volatile boolean isStarted = false;
    private final TaskRunner taskRunner;
    private final StringHashCodeTask task;

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
            if(Thread.interrupted()){
                Thread.currentThread().interrupt();
                isStopped = true;
                break;
            }
            String string = Utility.generateRandomString();
            Integer hashCode;
            try {
                hashCode = taskRunner.run(task, string).getValue();
            } catch (TaskRunnerIsDownException | TaskRunnerImplResultWaitInterruptedException ex){
                System.err.printf("Client received exception %s. Stopping...\n", ex.getClass().getSimpleName());
                isStopped = true;
                break;
            } catch (TaskRunnerRunException | TaskRunnerCancelledException ex){
                //Some logging, these exceptions are not expected to happen
                //However they could if one uses TaskRunner not in appropriate way
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
