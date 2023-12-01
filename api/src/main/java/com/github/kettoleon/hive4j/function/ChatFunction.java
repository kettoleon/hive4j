package com.github.kettoleon.hive4j.function;

import com.github.kettoleon.hive4j.agent.Agent;
import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;
import java.util.List;

public interface ChatFunction {

    void loadMessage(String chatName, ZonedDateTime time, Agent from, String msg);

    Flux<String> join(String chatName, GenerativeAgent agent, List<Agent> interlocutors);

    Flux<String> addMessage(String chatName, Agent from, GenerativeAgent to, String msg);

    Flux<String> close(String chatName, GenerativeAgent agent);

    void clearConversation(String chatName);
}
