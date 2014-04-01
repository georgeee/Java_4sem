package ru.georgeee.itmo.java.sem4.task6;

import ru.georgeee.itmo.java.sem4.common.Task;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerIsDownException;
import ru.georgeee.itmo.java.sem4.task6.exceptions.TaskRunnerRunException;

import java.util.concurrent.*;

/**
 * Created by georgeee on 01.04.14.
 */
public class TaskRunnerImpl implements ExtendedTaskRunner {
    public static final long DEFAULT_FUTURE_GET_TIMEOUT_MILLIS = 400;
    public static final long DEFAULT_AWAIT_TERMINATION_MILLIS = 5000;
    private ExecutorService executorService;
    private volatile boolean isSetForShutDown = false;

    private long futureGetTimeoutMillis;
    private long awaitTerminationMillis;

    public TaskRunnerImpl(int threadCount, long futureGetTimeoutMillis, long awaitTerminationMillis) {
        this.futureGetTimeoutMillis = futureGetTimeoutMillis;
        this.awaitTerminationMillis = awaitTerminationMillis;
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public TaskRunnerImpl(int threadCount) {
        this(threadCount, DEFAULT_FUTURE_GET_TIMEOUT_MILLIS, DEFAULT_AWAIT_TERMINATION_MILLIS);
    }

    @Override
    public void shutdown() {
        isSetForShutDown = true;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(awaitTerminationMillis, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(awaitTerminationMillis, TimeUnit.MILLISECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public <X, Y> X run(Task<X, Y> task, Y value) {
        if (isSetForShutDown) {
            throw new TaskRunnerIsDownException();
        }
        Future<X> future;
        try {
            future = executorService.submit(new TaskCallable<X, Y>(task, value));
        } catch (RejectedExecutionException e) {
            if (isSetForShutDown) {
                throw new TaskRunnerIsDownException(e);
            }
            throw new TaskRunnerRunException(e);
        }
        X result = null;
        try {
            while (result == null) {
                try {
                    result = future.get(futureGetTimeoutMillis, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    if (isSetForShutDown) {
                        future.cancel(true);
                        throw new TaskRunnerIsDownException(e);
                    }
                }
            }
            //Simple "result = future.get();" will cause current thread to be running forever if shutdown occurs
        } catch (InterruptedException e) {
            if (isSetForShutDown) {
                throw new TaskRunnerIsDownException(e);
            } else {
                throw new TaskRunnerRunException(e);
            }
        } catch (ExecutionException e) {
            throw new TaskRunnerRunException(e);
        }
        return result;
    }

    private static class TaskCallable<X, Y> implements Callable<X> {
        Task<X, Y> task;
        Y input;

        private TaskCallable(Task<X, Y> task, Y input) {
            this.task = task;
            this.input = input;
        }

        @Override
        public X call() throws Exception {
            return task.run(input);
        }
    }

}
