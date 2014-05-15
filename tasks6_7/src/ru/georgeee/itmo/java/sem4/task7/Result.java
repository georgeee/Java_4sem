package ru.georgeee.itmo.java.sem4.task7;

public class Result<V> {
    final V result;
    final Exception exception;

    Result(Exception exception) {
        this.exception = exception;
        this.result = null;
    }

    Result(V result) {
        this.exception = null;
        this.result = result;
    }
}
