package com.github.kettoleon.hive4j.model;

public enum ModelBackend {
    OLLAMA("Ollama", "http://ollama.ai/library");

    private final String name;
    private final String url;

    ModelBackend(String name, String url) {

        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
