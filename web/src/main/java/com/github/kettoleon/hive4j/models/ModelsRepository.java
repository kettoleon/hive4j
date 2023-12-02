package com.github.kettoleon.hive4j.models;

import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import com.github.kettoleon.hive4j.model.Model;
import com.github.kettoleon.hive4j.websocket.ModelPullHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.github.kettoleon.hive4j.model.ModelBackend.OLLAMA;

@Component
public class ModelsRepository {

    @Autowired
    private OLlamaClient oLlamaClient;

    @Autowired
    private ModelPullHandler modelPullHandler;

    public List<Model> getModels(){
        ArrayList<Model> models = new ArrayList<>();

        oLlamaClient.getModelList().stream().map(this::toUIModel).forEach(models::add);
        models.addAll(modelPullHandler.getPullingModels());

        return models;
    }

    public void pullModel(Model model) {
        modelPullHandler.pullModel(model);
    }

    public void deleteModel(Model model) {
        oLlamaClient.deleteModel(model.getRepositoryId());
    }

    private Model toUIModel(OLlamaModel oLlamaModel) {
        Model model = new Model();
        model.setBackend(OLLAMA);
        model.setRepositoryId(oLlamaModel.getName());
        model.setRepositoryUrl("http://ollama.ai/library/" + oLlamaModel.getFamily());
        model.setName(oLlamaModel.getName());
        model.setFamily(oLlamaModel.getFamily());
        model.setTag(oLlamaModel.getTag());
        model.setFileSize(oLlamaModel.getSize());
        model.setContextSize(oLlamaModel.getContextSize());
        //TODO waiting for some sort of ollama library api
        return model;
    }
}
