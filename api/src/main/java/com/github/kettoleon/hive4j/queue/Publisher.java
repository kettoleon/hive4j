package com.github.kettoleon.hive4j.queue;

import reactor.core.publisher.Mono;

/**
 * Abstraction for queues. Make different implementations for JMS, RabbitMQ, Kafka, etc. as needed.
 */
public interface Publisher<M, R> {

    void fireAndForget(Message<M> message);

    Mono<Message<R>> requestAndReply(Message<M> message);

}
