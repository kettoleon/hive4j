package com.github.kettoleon.hive4j.clients.ollama.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ModelsResponse {

    private List<OLlamaModel> models;
}
