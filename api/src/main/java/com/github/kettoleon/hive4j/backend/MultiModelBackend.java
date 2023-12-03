package com.github.kettoleon.hive4j.backend;

public interface MultiModelBackend extends Backend {


    default boolean isModelPullingCapable() {
        return true;
    }

    default boolean isModelDeletingCapable() {
        return true;
    }
}
