package ru.georgeee.itmo.java.sem4.task7;

public class Result<V> {
    V result;
    Exception exception;

    Result(Exception exception) {
        this.exception = exception;
    }

    Result(V result) {
        this.result = result;
    }
}
