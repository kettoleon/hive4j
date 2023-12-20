package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadStatus {

    private String error;
    private String status;
    private String digest;
    private long total;
    private long completed;

    public boolean isDownloading() {
        return status != null && status.startsWith("pulling");
    }

    public boolean isSuccess() {
        return status != null && status.equals("success");
    }

    public boolean isError() {
        return error != null;
    }
}
