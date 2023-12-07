package com.github.kettoleon.hive4j.queue;

import java.util.HashMap;
import java.util.Map;

public class FIFOLock {

    private int blockedThreads = 0;
    private int currentTurn = 0;
    private int nextTurn = 0;

    private final Map<Thread, Integer> threadTurns = new HashMap<>();

    public synchronized void await() {
        if (blockedThreads == 0) {
            currentTurn = 0;
            nextTurn = 0;
        }
        blockedThreads++;
        threadTurns.put(Thread.currentThread(), nextTurn);
        nextTurn++;

        while (isNotMyTurn()) {
            try {
                wait(1000L);
            } catch (InterruptedException ignored) {
            }
        }
        threadTurns.remove(Thread.currentThread());
    }

    private boolean isNotMyTurn() {
        return threadTurns.get(Thread.currentThread()) != currentTurn;
    }

    public synchronized void done() {
        blockedThreads--;
        currentTurn++;
        notifyAll();
    }

}
