package ru.georgeee.itmo.java.sem4.task6;

import ru.georgeee.itmo.java.sem4.common.Task;

/**
 * Created by georgeee on 01.04.14.
 */
public interface TaskRunner {
    <X, Y> X run(Task<X, Y> task, Y value);
}
