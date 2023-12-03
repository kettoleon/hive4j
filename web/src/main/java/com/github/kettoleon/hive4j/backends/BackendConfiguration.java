package com.github.kettoleon.hive4j.backends;

import lombok.Data;

import java.util.Map;

@Data
public class BackendConfiguration {

    private String id;
    private String name;
    private String type;

    private Map<String, Object> config;

}
