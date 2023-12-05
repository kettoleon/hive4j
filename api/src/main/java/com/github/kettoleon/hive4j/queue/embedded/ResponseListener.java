package com.github.kettoleon.hive4j.queue.embedded;

import com.github.kettoleon.hive4j.queue.Listener;
import com.github.kettoleon.hive4j.queue.Message;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class ResponseListener<R> implements Listener<R, Void> {

    private final MonoSink<Message<R>> monoSink;
    private final EmbeddedQueueChannelThread<R, Void> responseThread;
    private final String requestId;

    public ResponseListener(MonoSink<Message<R>> monoSink, EmbeddedQueueChannelThread<R, Void> responseThread, String requestId) {

        this.monoSink = monoSink;
        this.responseThread = responseThread;
        this.requestId = requestId;
    }

    @Override
    public Mono<Message<Void>> onMessage(Message<R> resp) {
        if (resp.requestId().equals(requestId)) {
            responseThread.removeConsumer(this);
            monoSink.success(resp);
        }
        return Mono.empty();
    }
}