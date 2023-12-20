package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLlamaModelDetails {

    private String format;
    private List<String> families;

    @JsonProperty("parameter_size")
    private String parameterSize;

    @JsonProperty("quantization_level")
    private String quantizationLevel;

}
