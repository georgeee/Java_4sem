package ru.georgeee.itmo.java.sem4.task7;

public class Producer<V> implements Runnable {
    Producer(Environment<V> environment) {
        this.environment = environment;
    }

    Environment<V> environment;

    @Override
    public void run() {
        while (!environment.isShutdown() && !Thread.interrupted()) {
            try {
                environment.producerConsumerBus.put(environment.factory.generateCallable());
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
