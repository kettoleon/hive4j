package com.github.kettoleon.hive4j.clients.ollama.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmbeddingsRequest {

    private String model;
    private String prompt;

    //TODO options
}
