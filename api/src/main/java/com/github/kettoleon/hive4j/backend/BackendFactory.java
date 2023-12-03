package com.github.kettoleon.hive4j.backend;

import java.util.Map;

public interface BackendFactory {

    Backend createBackend(String id, String name, Map<String, Object> config);
}
