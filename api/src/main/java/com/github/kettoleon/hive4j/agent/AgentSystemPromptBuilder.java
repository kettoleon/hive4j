package com.github.kettoleon.hive4j.agent;


/**
 * Your application might want to have specific fields for its agents, but we will need a way to serialise agent
 * information into system prompts.
 * @param <A> The agent subclass.
 */
public interface AgentSystemPromptBuilder<A extends Agent> {

    String buildSystemPrompt(A agent, Perspective perspective);

}
