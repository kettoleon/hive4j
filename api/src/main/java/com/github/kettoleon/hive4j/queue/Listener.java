package com.github.kettoleon.hive4j.queue;

import reactor.core.publisher.Mono;

/**
 * Abstraction for queues. Invoke this from your JMS Listeners for example.
 */
public interface Listener<M, R> {

    Mono<Message<R>> onMessage(Message<M> message);

}
