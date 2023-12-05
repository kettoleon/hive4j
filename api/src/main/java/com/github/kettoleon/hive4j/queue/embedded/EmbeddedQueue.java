package com.github.kettoleon.hive4j.queue.embedded;

import com.github.kettoleon.hive4j.queue.Message;
import com.github.kettoleon.hive4j.queue.Publisher;
import reactor.core.publisher.Mono;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EmbeddedQueue<M, R> implements Publisher<M, R> {

    private final BlockingQueue<Message<M>> requestQueue;
    private final BlockingQueue<Message<R>> responseQueue;
    private final EmbeddedQueueChannelThread<R, Void> responseThread;

    public EmbeddedQueue() {
        requestQueue = new LinkedBlockingQueue<>();
        responseQueue = new LinkedBlockingQueue<>();
        responseThread = new EmbeddedQueueChannelThread<>(responseQueue);
        responseThread.start();
    }

    public Message<M> take() throws InterruptedException {
        return requestQueue.take();
    }

    public void reply(Message<R> message) {
        responseQueue.add(message);
    }

    @Override
    public void fireAndForget(Message<M> message) {
        requestQueue.add(message);
    }

    @Override
    public Mono<Message<R>> requestAndReply(Message<M> message) {
        return Mono.create((ms) -> {
            responseThread.addConsumer(new ResponseListener<>(ms, responseThread, message.requestId()));
            requestQueue.add(message);
        });
    }

}
