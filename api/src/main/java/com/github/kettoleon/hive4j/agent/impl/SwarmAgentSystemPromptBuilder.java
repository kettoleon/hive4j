package com.github.kettoleon.hive4j.agent.impl;


import com.github.kettoleon.hive4j.agent.AgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agent.Perspective;
import com.github.kettoleon.hive4j.agent.SwarmAgent;

import java.util.List;

import static java.lang.String.join;

public class SwarmAgentSystemPromptBuilder implements AgentSystemPromptBuilder<SwarmAgent> {

    @Override
    public String buildSystemPrompt(SwarmAgent agent, Perspective perspective) {
        if(!perspective.equals(Perspective.SECOND_PERSON)){
            throw new UnsupportedOperationException("We only support second person for now...");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("You are ").append(agent.getName()).append("\n");
        sb.append("Your user id is ").append(agent.getId()).append("\n");
        sb.append("Your mission is ").append(agent.getMission()).append("\n");
        sb.append("You are an AI assistant, you are part of an AI agent swarm\n");
        sb.append("Your function in the swarm is ").append(agent.getMission()).append("\n");

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
