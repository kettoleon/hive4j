package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.backend.Backend;
import com.github.kettoleon.hive4j.backend.BackendFactory;
import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ollamaBackendFactory")
public class OLlamaBackendFactory implements BackendFactory {

    @Override
    public Backend createBackend(String id, String name, Map<String, Object> config) {
        return new OLlamaBackend(id, name, new OLlamaClient(config.get("httpUrl").toString()));
    }
}
