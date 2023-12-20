package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class GenerateResponse {

    private String model;
    private String response;
    private boolean done;

    @JsonProperty("total_duration")
    private long totalDuration;

    @JsonProperty("load_duration")
    private long load_duration;

    @JsonProperty("sample_count")
    private int sampleCount;

    @JsonProperty("sample_duration")
    private long sampleDuration;

    @JsonProperty("prompt_eval_count")
    private int promptEvalCount;

    @JsonProperty("prompt_eval_duration")
    private long promptEvalDuration;

    @JsonProperty("eval_count")
    private int evalCount;

    @JsonProperty("eval_duration")
    private long evalDuration;

    public double getTokensPerSecond() {
        return evalCount / getEvalDurationInSeconds();
    }

    public double getEvalDurationInSeconds() {
        return evalDuration / 1000000000.0;
    }

    public String getHumanReadableTokensPerSecond() {
        return String.format("%.2f", getTokensPerSecond());
    }
}
