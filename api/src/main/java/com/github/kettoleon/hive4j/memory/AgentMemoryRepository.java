package com.github.kettoleon.hive4j.memory;


import com.github.kettoleon.hive4j.agent.GenerativeAgent;

public interface AgentMemoryRepository {
    MemoryStream getMemoryStream(GenerativeAgent agent);

    void saveMemoryStream(MemoryStream memoryStream, GenerativeAgent agent);

}
