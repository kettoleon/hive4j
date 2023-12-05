package com.github.kettoleon.hive4j.queue.embedded;

import com.github.kettoleon.hive4j.queue.Listener;
import com.github.kettoleon.hive4j.queue.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmbeddedQueueChannelThread<M, R> extends Thread {

    private final BlockingQueue<Message<M>> queue;
    private final List<Listener<M, R>> consumers = new CopyOnWriteArrayList<>();

    public EmbeddedQueueChannelThread(BlockingQueue<Message<M>> queue) {
        this.queue = queue;
    }

    public void run() {

        while (true) {

            Message<M> message = queue.poll();

            if (message != null) {
                //TODO traceability
                consumers.forEach(c -> c.onMessage(message));
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void addConsumer(Listener<M, R> consumer) {
        consumers.add(consumer);
    }

    public void removeConsumer(Listener<M, R> consumer) {
        consumers.remove(consumer);
    }

}
