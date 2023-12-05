package com.github.kettoleon.hive4j.queue.embedded;

import com.github.kettoleon.hive4j.queue.Listener;

public class EmbeddedListenerThread<M, R> extends Thread {

    private final EmbeddedQueue<M, R> queue;
    private final Listener<M, R> listener;

    public EmbeddedListenerThread(EmbeddedQueue<M, R> queue, Listener<M, R> listener) {
        this.queue = queue;
        this.listener = listener;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                listener.onMessage(queue.take())
                        .subscribe(queue::reply);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
