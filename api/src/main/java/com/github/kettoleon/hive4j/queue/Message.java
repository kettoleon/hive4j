package com.github.kettoleon.hive4j.queue;

public record Message<M>(String correlationId, String requestId, M message) {

}
