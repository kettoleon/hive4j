package com.github.kettoleon.hive4j.backend;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.kettoleon.hive4j.util.BytesFormatter.toHumanReadableDownloadBytes;

@Data
@NoArgsConstructor
public class ModelPullingStatus {

    private String error;
    private String status;
    private long total;
    private long completed;

    private boolean downloading;
    private boolean success;

    public String getHumanReadableDownloadProgress() {
        return String.format("%s of %s downloaded", toHumanReadableDownloadBytes(completed), toHumanReadableDownloadBytes(total));
    }

    public String getCompletedPercentage() {
        if(total != 0) {
            return String.format("%d", (completed * 100) / total);
        }
        return "0";
    }

    public boolean isError() {
        return error != null;
    }
}