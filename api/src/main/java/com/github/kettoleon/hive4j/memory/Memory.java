package com.github.kettoleon.hive4j.memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Memory {

    private String id;
    private ZonedDateTime created;
    private ZonedDateTime lastAccessed;
    private String description;
    private MemoryType memoryType;
    private double importance;

    private double[] embeddingVector;


    public String getId() {
        return id;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getLastAccessed() {
        return lastAccessed;
    }

    public String getDescription() {
        return description;
    }

    public MemoryType getMemoryType() {
        return memoryType;
    }

    public double getImportance() {
        return importance;
    }

    public double[] getEmbeddingVector() {
        return embeddingVector;
    }


    void setId(String id) {
        this.id = id;
    }

    void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    void setLastAccessed(ZonedDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setMemoryType(MemoryType memoryType) {
        this.memoryType = memoryType;
    }

    void setImportance(double importance) {
        this.importance = importance;
    }

    void setEmbeddingVector(double[] embeddingVector) {
        this.embeddingVector = embeddingVector;
    }

}
