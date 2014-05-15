package ru.georgeee.itmo.java.sem4.task6.executor;

import ru.georgeee.itmo.java.sem4.task6.interfaces.Task;

import java.util.LinkedList;

public class Executor {
    private volatile boolean forShutdown = false;
    private final LinkedList<FutureTask> queue;

    public Executor(int threadCount) {
        queue = new LinkedList<>();
        threads = new ExecutorThread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new ExecutorThread();
            threads[i].start();
        }
    }

    private final ExecutorThread[] threads;

    public void shutdown(boolean needInterrupt) {
        forShutdown = true;
        synchronized (queue) {
            for (FutureTask task : queue){
                task.cancel();
                synchronized (task){
                    task.notifyAll();
                }
            }
            queue.clear();
            queue.notifyAll();
        }
        if (needInterrupt) {
            for (Thread thread : threads) thread.interrupt();
        }
    }

    public void shutdown() {
        shutdown(false);
    }

    public <X, Y> FutureTask<X, Y> submit(Task<X, Y> task, Y argument) {
        return submit(new FutureTask<X, Y>(task, argument));
    }

    public <X, Y> FutureTask<X, Y> submit(FutureTask<X, Y> futureTask) {
        if (forShutdown) return null;
        synchronized (queue) {
            if (forShutdown) return null;
            queue.add(futureTask);
            queue.notify();
        }
        return futureTask;
    }

    private class ExecutorThread extends Thread {
        @Override
        public void run() {
            while (!forShutdown) {
                if(isInterrupted()){
                    interrupt();
                    break;
                }
                FutureTask futureTask = null;
                while (futureTask == null && !forShutdown) {
                    if (isInterrupted()) {
                        interrupt();
                        break;
                    }
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                interrupt();
                                break;
                            }
                        }
                        if (!queue.isEmpty()) futureTask = queue.removeLast();
                    }
                }
                if(forShutdown && futureTask != null){
                    queue.addLast(futureTask);
                    break;
                }else if (isInterrupted()) {
                    interrupt();
                    if (futureTask != null) {
                        queue.addLast(futureTask);
                    }
                    break;
                }
                futureTask.execute();
                synchronized (futureTask){
                    futureTask.notifyAll();
                }
            }
        }
    }
}
