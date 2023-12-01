package com.github.kettoleon.hive4j.memory;


import com.github.kettoleon.hive4j.agent.GenerativeAgent;

import java.time.ZonedDateTime;
import java.util.List;

public interface MemoryStream {

    Memory addMemory(MemoryType type, String description);

    void setMemories(List<Memory> memories);

    List<Memory> getMemories();

    List<Memory> getMemoriesByCreatedDesc();

    List<Memory> getMemoriesByRelevanceDesc(String query);

    /**
     * You will most likely want to fill the prompt with as many memories as possible. The iterator will be used
     * so that the MemoryStream knows which ones have been accessed to keep them as "recent".
     * Yes, we know, the last one requested will not be actually used, but it is fine.
     *
     * @param query descriptive query in natural language to search for relevant memories. i.e. the task of the agent at hand
     * @return An iterator that wil return pairs with the score (From 1.0 to 0.0) and the memory.
     */
    List<Memory> findMemoriesByRelevance(String query, int limit, double threshold);

    List<Memory> findMemoriesByCreated(GenerativeAgent agent, int limit);

    List<Memory> findMemoriesByTimePeriod(ZonedDateTime from, ZonedDateTime to);

}
