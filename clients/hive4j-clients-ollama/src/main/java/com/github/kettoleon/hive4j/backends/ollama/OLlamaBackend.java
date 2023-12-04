package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.backend.ModelPullingStatus;
import com.github.kettoleon.hive4j.backend.MultiModelBackend;
import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.clients.ollama.model.DownloadStatus;
import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import com.github.kettoleon.hive4j.model.Model;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class OLlamaBackend implements MultiModelBackend {

    private static final String OLLAMA_AI_LIBRARY = "http://ollama.ai/library/";
    private final String id;
    private final String name;
    private final OLlamaClient client;
    private final List<Model> pullingModels = new ArrayList<>();

    public OLlamaBackend(String id, String name, OLlamaClient client) {
        this.id = id;
        this.name = name;
        this.client = client;
    }

    @Override
    public List<Model> getAvailableModels() {
        List<Model> installedModels = client.getModelList().stream().map(this::toAppModel).collect(toList());
        ArrayList<Model> models = new ArrayList<>();
        models.addAll(installedModels);
        models.addAll(pullingModels);
        return models;
    }

    @Override
    public Model getModel(String backendModelId) {
        return getAvailableModels().stream().filter(model -> model.getBackendId().equals(backendModelId)).findFirst().orElse(downloadingModel(backendModelId));
    }

    private Model downloadingModel(String backendModelId) {
        Model model = new Model();
        model.setBackend(this);
        model.setBackendId(backendModelId);
        model.setFamily(backendModelId.split(":")[0]);
        model.setRepositoryUrl(OLLAMA_AI_LIBRARY + model.getFamily());
        model.setName(backendModelId);
        return model;
    }

    @Override
    public double[] embeddings(Model model, String text) {
        return client.embeddings(model.getBackendId(), text);
    }

    @Override
    public int countTokens(Model model, String text) {
        return embeddings(model, text).length;
    }

    @Override
    public Flux<String> generate(Model model, String rawPrompt) {
        return client.generate(model.getBackendId(), rawPrompt); //TODO profiles
    }

    @Override
    public Flux<ModelPullingStatus> pullModel(Model model) {
        pullingModels.add(model);
        return client.pullModel(model.getBackendId())
                .doFinally(signalType -> pullingModels.remove(model))
                .map(this::toAppModel);
    }

    @Override
    public void deleteModel(Model model) {
        client.deleteModel(model.getBackendId());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    private ModelPullingStatus toAppModel(DownloadStatus downloadStatus) {
        ModelPullingStatus modelPullingStatus = new ModelPullingStatus();
        modelPullingStatus.setError(downloadStatus.getError());
        modelPullingStatus.setStatus(downloadStatus.getStatus());
        modelPullingStatus.setTotal(downloadStatus.getTotal());
        modelPullingStatus.setCompleted(downloadStatus.getCompleted());
        modelPullingStatus.setDownloading(downloadStatus.isDownloading());
        modelPullingStatus.setSuccess(downloadStatus.isSuccess());
        return modelPullingStatus;
    }

    private Model toAppModel(OLlamaModel oLlamaModel) {
        Model model = new Model();
        model.setBackend(this);
        model.setBackendId(oLlamaModel.getName());
        model.setRepositoryUrl(OLLAMA_AI_LIBRARY + oLlamaModel.getFamily());
        model.setName(oLlamaModel.getName());
        model.setFamily(oLlamaModel.getFamily());
        model.setTag(oLlamaModel.getTag());
        model.setFileSize(oLlamaModel.getSize());
        model.setContextSize(oLlamaModel.getContextSize());
        //TODO waiting for some sort of ollama library api
        return model;
    }
}
