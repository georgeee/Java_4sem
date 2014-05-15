package ru.georgeee.itmo.java.sem4.task6.misc;

import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerCancelledException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerImplResultWaitInterruptedException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerIsDownException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerRunException;
import ru.georgeee.itmo.java.sem4.task6.executor.Executor;
import ru.georgeee.itmo.java.sem4.task6.executor.FutureTask;
import ru.georgeee.itmo.java.sem4.task6.interfaces.ExtendedTaskRunner;
import ru.georgeee.itmo.java.sem4.task6.interfaces.Task;


public class TaskRunnerImpl implements ExtendedTaskRunner {
    private volatile boolean isSetForShutDown = false;
    private final Executor executor;


    public TaskRunnerImpl(int threadCount) {
        executor = new Executor(threadCount);
    }

    @Override
    public void shutdown() {
        isSetForShutDown = true;
        executor.shutdown(true);
    }

    @Override
    public <X, Y> X run(Task<X, Y> task, Y value) {
        if (isSetForShutDown) {
            throw new TaskRunnerIsDownException();
        }
        FutureTask<X, Y> futureTask;
        if ((futureTask = executor.submit(task, value)) == null) {
            if (isSetForShutDown) {
                throw new TaskRunnerIsDownException();
            }
            throw new TaskRunnerRunException();
        }
        try {
            while (futureTask.isPendingOrRunning() && !isSetForShutDown) {
                synchronized (futureTask) {
                    if (futureTask.isPendingOrRunning()) {
                        futureTask.wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskRunnerImplResultWaitInterruptedException(e);
        }
        if (futureTask.isReady()) {
            return futureTask.getResult();
        }
        if (isSetForShutDown) {
            throw new TaskRunnerIsDownException();
        }
        if (futureTask.isCancelled()) {
            throw new TaskRunnerCancelledException();
        }
        throw new TaskRunnerRunException(); //Won't happen
    }

}
