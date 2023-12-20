package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
@Builder
public class GenerateRequest {

    private String model;
    private String prompt;

    private String format;
    private String system;
    private Options options;
    private String template;
    private String context;
    private boolean stream;
    private boolean raw;

    //For multimodal models, base64 encoded png or jpg images (up to 100MB each)
    private List<String> images;
}
