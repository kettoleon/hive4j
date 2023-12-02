package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadStatus {

    private String error;
    private String status;
    private String digest;
    private long total;
    private long completed;

    public String getHumanReadableDownloadProgress() {
        return String.format("%s of %s downloaded", byteCountToDisplaySize(completed), byteCountToDisplaySize(total));
    }

    public String getCompletedPercentage() {
        if(total != 0) {
            return String.format("%d", (completed * 100) / total);
        }
        return "0";
    }

    public boolean isPulling() {
        return status != null && status.startsWith("pulling");
    }

    public boolean isSuccess() {
        return status != null && status.equals("success");
    }

    public boolean isError() {
        return error != null;
    }
}
