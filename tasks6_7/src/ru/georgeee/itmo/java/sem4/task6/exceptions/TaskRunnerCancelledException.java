package ru.georgeee.itmo.java.sem4.task6.exceptions;

/**
 * Created by georgeee on 01.04.14.
 */
public class TaskRunnerCancelledException extends RuntimeException {
    public TaskRunnerCancelledException() {
    }

    public TaskRunnerCancelledException(Throwable cause) {
        super(cause);
    }
}
