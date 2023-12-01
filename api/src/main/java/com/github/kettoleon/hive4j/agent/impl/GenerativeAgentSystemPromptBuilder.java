package com.github.kettoleon.hive4j.agent.impl;


import com.github.kettoleon.hive4j.agent.AgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import com.github.kettoleon.hive4j.agent.Perspective;

import java.util.List;

import static java.lang.String.join;

public class GenerativeAgentSystemPromptBuilder implements AgentSystemPromptBuilder<GenerativeAgent> {

    @Override
    public String buildSystemPrompt(GenerativeAgent agent, Perspective perspective) {
        if(!perspective.equals(Perspective.SECOND_PERSON)){
            throw new UnsupportedOperationException("We only support second person for now...");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("You are ").append(agent.getName()).append("\n");
        sb.append("Your user id is ").append(agent.getId()).append("\n");

        for(String statement : agent.getStatements()){
            sb.append("You are ").append(statement).append("\n");
        }

        if(notNullNotEmpty(agent.getTraits())){
            sb.append("You are ").append(join(", ", agent.getTraits())).append("\n");
        }

        return sb.toString();
    }

    private boolean notNullNotEmpty(List<String> traits) {
        return traits != null && !traits.isEmpty();
    }
}
