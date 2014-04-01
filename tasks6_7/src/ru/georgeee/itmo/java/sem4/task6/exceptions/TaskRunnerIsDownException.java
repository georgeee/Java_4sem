package ru.georgeee.itmo.java.sem4.task6.exceptions;

/**
 * Created by georgeee on 01.04.14.
 */
public class TaskRunnerIsDownException extends RuntimeException {
    public TaskRunnerIsDownException(Throwable cause) {
        super(cause);
    }

    public TaskRunnerIsDownException() {

    }
}


