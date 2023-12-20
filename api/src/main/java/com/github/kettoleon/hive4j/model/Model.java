package com.github.kettoleon.hive4j.model;

import com.github.kettoleon.hive4j.backend.Backend;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.github.kettoleon.hive4j.util.BytesFormatter.toHumanReadableBytes;
import static com.github.kettoleon.hive4j.util.BytesFormatter.toHumanReadableSuffix;

@Data
@NoArgsConstructor
public class Model {

    private Backend backend;
    private String backendId;
    private String repositoryUrl;

    private String name;
    private String family;
    private String tag;

    private long fileSize;
    private int contextSize;

    private LicenseType licenseType;

    private String parameters;
    private String quantization;
    private String format;

    private String pullProgressId;

    private InstructionSerializer instructionSerializer;

    public String getHumanReadableFileSize() {
        return toHumanReadableBytes(fileSize);
    }

    public String getHumanReadableContextSize() {
        return toHumanReadableSuffix(contextSize);
    }

    public boolean isPulling() {
        return pullProgressId != null;
    }

    public String getHtmlId() {
        return toSafeCssIdentifier(backend.getId() + "-" + backendId);
    }

    public static String toSafeCssIdentifier(String str) {
        return str.replaceAll("[^a-zA-Z0-9\\-_]", "_");
    }

    public double[] embeddings(String text) {
        return backend.embeddings(this, text);
    }

    public int countTokens(String text) {
        return backend.countTokens(this, text);
    }

    public Flux<String> generate(String rawPrompt) {
        return backend.generate(this, rawPrompt);
    }

    public Flux<String> generate(Instruction instruction) {
        return generate(instructionSerializer.serialize(List.of(instruction)))
                .startWith(instruction.shouldIncludeResponseStart() ? instruction.getForcedResponseStart() : "");
    }

}
