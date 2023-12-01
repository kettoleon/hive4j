package com.github.kettoleon.hive4j.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Instruction {

    private String system;
    private String instruction;
    private String input;
    private String response;
    private String forcedResponseStart;
    private boolean excludeResponseStart;

    private Integer tokenCount;

    //Higher values: more intelligent, less performant
    //We set the higher value for now, since we realised that locally, we need to make the llm as smart as possible
    //despite the performance for most applications. However, occasionally, some easy/simpler requests might benefit
    //from performance on the long run.
    @Builder.Default
    private double intelligenceTradeoff = 1;

}
