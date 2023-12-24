package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.backend.Backend;
import com.github.kettoleon.hive4j.backend.BackendFactory;
import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.clients.ssh.SshPortForwardingFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("ollamaBackendFactory")
public class OLlamaBackendFactory implements BackendFactory {

    @Autowired
    private SshPortForwardingFactory sshPortForwardingFactory;

    @Override
    public Backend createBackend(String id, String name, Map<String, Object> config) {
        sshPortForwardingFactory.startPortForwardingFromConfigIfNeeded(config);
        return new OLlamaBackend(id, name, new OLlamaClient(config.get("httpUrl").toString()));
    }
}
