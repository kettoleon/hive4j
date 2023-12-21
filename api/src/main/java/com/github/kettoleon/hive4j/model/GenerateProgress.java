package com.github.kettoleon.hive4j.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateProgress {

    private Model modelUsed;
    private GenerateRequest request;
    private ZonedDateTime created;
    private ZonedDateTime started;

    private boolean done;
    private StringBuffer fullResponse;
    private String partResponse;
    private int responseTokens;
    private long responseMillis;

    private String errorMessage;
    private String errorDetails;

    public String getFullResponse() {
        return fullResponse.toString();
    }

    public StringBuffer getFullResponseBuffer() {
        return fullResponse;
    }

    public boolean isError() {
        return errorMessage != null;
    }


    public double getTokensPerSecond() {
        return responseTokens / getResponseSeconds();
    }

    public double getResponseSeconds() {
        if (responseMillis == 0) {
            return Duration.between(started, ZonedDateTime.now()).toMillis() / 1000.0;
        }
        return responseMillis / 1000.0;
    }

    public String getHumanReadableTokensPerSecond() {
        return String.format("%.2f", getTokensPerSecond());
    }

}
