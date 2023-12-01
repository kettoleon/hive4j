package com.github.kettoleon.hive4j.agent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * At this level, generative agents can still be intended for human behaviour simulation or mission-oriented purposes.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class SwarmAgent extends GenerativeAgent {

    /**
     * Brief description on what they specialise, so that they can figure out who to talk to by themselves.
     */
    private String specialty;

    /**
     * Their purpose.
     */
    private String mission;

    /**
     * Their function is how they serve the swarm.
     */
    private String function;



}
