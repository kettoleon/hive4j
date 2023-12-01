package com.github.kettoleon.hive4j.function;

import com.github.kettoleon.hive4j.agent.GenerativeAgent;

public interface MemoryRatingFunction {

    /**
     * @param agent
     * @param memory
     * @return a value between 0.0 and 1.0 (less to most important)
     */
    double rateMemory(GenerativeAgent agent, String memory);

}
