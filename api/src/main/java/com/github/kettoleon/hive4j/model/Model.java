package com.github.kettoleon.hive4j.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;

@Data
@NoArgsConstructor
public class Model {

    private ModelBackend backend;
    private String repositoryId;
    private String repositoryUrl;

    private String name;
    private String family;
    private String tag;

    private long fileSize;
    private String contextSize;

    private LicenseType licenseType;

    private int parametersInBillions;
    private String quantization;

    private String pullProgressId;

    public String getHumanReadableFileSize() {
        return FileUtils.byteCountToDisplaySize(fileSize);
    }

    public boolean isPulling() {
        return pullProgressId != null;
    }

}
