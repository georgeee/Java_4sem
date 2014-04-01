package ru.georgeee.itmo.java.sem4.task7;

import java.util.concurrent.Callable;

public class Consumer<V> implements Runnable {
    Environment<V> environment;

    Consumer(Environment<V> environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        while (!environment.isShutdown()) {
            Result<V> result;
            try {
                Callable<V> callable = environment.producerConsumerBus.take();
                result = new Result(callable.call());
            } catch (Exception e) {
                result = new Result(e);
            }
            try {
                environment.consumerPublisherBus.put(result);
            } catch (InterruptedException e) {
            }
        }
    }
}
