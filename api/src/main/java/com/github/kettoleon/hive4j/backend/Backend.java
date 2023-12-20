package com.github.kettoleon.hive4j.backend;

import com.github.kettoleon.hive4j.model.Model;
import reactor.core.publisher.Flux;

import java.util.List;

public interface Backend {

    String getId();

    String getName();

    String getVersion();

    List<Model> getAvailableModels();

    Model getModel(String backendModelId);

    double[] embeddings(Model model, String text);

    int countTokens(Model model, String text);

    Flux<String> generate(Model model, String rawPrompt);

    //TODO getSupportedModels (from a library, registry)

    Flux<ModelPullingStatus> pullModel(Model model);

    void deleteModel(Model model);

    default boolean isModelPullingCapable(){
        return false;
    }

    default boolean isModelDeletingCapable(){
        return false;
    }
}
