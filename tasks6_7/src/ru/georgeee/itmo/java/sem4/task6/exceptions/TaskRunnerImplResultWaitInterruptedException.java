package ru.georgeee.itmo.java.sem4.task6.exceptions;

/**
 * Created by georgeee on 01.04.14.
 */
public class TaskRunnerImplResultWaitInterruptedException extends RuntimeException {
    public TaskRunnerImplResultWaitInterruptedException() {
    }

    public TaskRunnerImplResultWaitInterruptedException(Throwable cause) {
        super(cause);
    }
}
