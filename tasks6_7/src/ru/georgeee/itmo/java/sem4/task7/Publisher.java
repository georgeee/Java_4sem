package ru.georgeee.itmo.java.sem4.task7;

public class Publisher<V> implements Runnable {
    Environment <V> environment;

    Publisher(Environment<V> environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        while (!environment.isShutdown()) {
            Result<V> result;
            try {
                result = environment.consumerPublisherBus.take();
                if (result.exception != null) {
                    result.exception.printStackTrace();
                } else {
                    System.out.println(result.result);
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
