package ru.georgeee.itmo.java.sem4.task7;

import ru.georgeee.itmo.java.sem4.common.CallableFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Environment<V> implements Runnable{
    final BlockingQueue<Callable<V>> producerConsumerBus;
    final BlockingQueue<Result> consumerPublisherBus;
    final CallableFactory<V> factory;
    private volatile boolean shutdown = false;
    final List<Thread> threads;

    public Environment(CallableFactory<V> factory, int producerCount, int consumerCount, int publisherCount) {
        this.factory = factory;
        producerConsumerBus = new LinkedBlockingQueue<>(consumerCount);
        consumerPublisherBus = new LinkedBlockingQueue<>(publisherCount);
        threads = new ArrayList<>();
        for (int i = 0; i < publisherCount; ++i) {
            threads.add(new Thread(new Publisher<>(this)));
        }
        for (int i = 0; i < consumerCount; ++i) {
            threads.add(new Thread(new Consumer<>(this)));
        }
        for (int i = 0; i < producerCount; ++i) {
            threads.add(new Thread(new Producer<>(this)));
        }
    }

    public void shutdown() {
        shutdown = true;
        producerConsumerBus.clear();
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void run() {
        for(Thread thread : threads){
            thread.start();
        }
    }
}
