package ru.georgeee.itmo.java.sem4.task6.executor;

import ru.georgeee.itmo.java.sem4.task6.interfaces.Task;

public class FutureTask<X, Y>  {
    private static final int PENDING_STATE = 0;
    private static final int RUNNING_STATE = 1;
    private static final int READY_STATE = 2;
    private static final int CANCELLED_STATE = 3;

    private final Task<X, Y> task;
    private final Y argument;

    private volatile X result;
    private volatile int state = PENDING_STATE;

    public FutureTask(Task<X, Y> task, Y argument) {
        this.task = task;
        this.argument = argument;
    }

    public void execute(){
        boolean needRunning = true;
        if(state != PENDING_STATE) needRunning = false;
        synchronized (this){
            if(state != PENDING_STATE) needRunning = false;
            else state = RUNNING_STATE;
        }
        if(!needRunning) return;
        X result = task.run(argument);
        if(state != RUNNING_STATE) return;
        synchronized (this){
            if(state != RUNNING_STATE) return;
            this.result = result;
            state = READY_STATE;
        }
    }

    public X getResult() {
        if(isReady())
            return result;
        return null;
    }

    public boolean isReady() {
        return state == READY_STATE;
    }

    public boolean isCancelled() {
        return state == CANCELLED_STATE;
    }

    public boolean isPendingOrRunning(){
        return state == PENDING_STATE || state == RUNNING_STATE;
    }

    public void cancel(){
        if(!isReady() && !isCancelled())
            synchronized (this){
                if(!isReady() && !isCancelled())
                    state = CANCELLED_STATE;
            }
    }
}
