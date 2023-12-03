package com.github.kettoleon.hive4j.backends;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "hive4j")
public class ConfiguredBackends {

    private Map<String, BackendConfiguration> backends;

}
