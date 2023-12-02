package com.github.kettoleon.hive4j.controller;

import com.github.kettoleon.hive4j.model.Model;
import com.github.kettoleon.hive4j.models.ModelsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;
import static com.github.kettoleon.hive4j.model.ModelBackend.OLLAMA;

@Controller
@Slf4j
public class WebController {

    @Autowired
    private ModelsRepository modelsRepository;

    @GetMapping(path = {"", "/", "/home"})
    public ModelAndView dashboard() {
        return page("home", "Home");
    }

    @GetMapping(path = {"/models"})
    public ModelAndView models() {
        //TODO add the ones that are still downloading...
        return page("models/models", "Models")
                .addObject("models", modelsRepository.getModels());
    }

    @GetMapping(path = {"/agents"})
    public ModelAndView agents() {
        return page("agents/agents", "Agents");
    }

    @PostMapping(path = {"/api/v1/models"})
    public ModelAndView pullModel(@RequestParam("modelTag") String modelTag) {
        Model model = ollamaModel(modelTag);
        modelsRepository.pullModel(model);
        return new ModelAndView("models/model-card").addObject("model", model);
    }

    @DeleteMapping(path = {"/api/v1/models/{modelTag}"})
    public ModelAndView deleteModel(@PathVariable("modelTag") String modelTag) {
        modelsRepository.deleteModel(ollamaModel(modelTag));
        return new ModelAndView("models/model-cards").addObject("models", modelsRepository.getModels());
    }

    private Model ollamaModel(String modelTag) {
        Model model = new Model();
        model.setBackend(OLLAMA);
        model.setRepositoryId(modelTag);
        model.setFamily(modelTag.split(":")[0]);
        model.setRepositoryUrl("http://ollama.ai/library/" + model.getFamily());
        model.setName(modelTag);
        model.setPullProgressId(UUID.randomUUID().toString());
        return model;
    }

}
