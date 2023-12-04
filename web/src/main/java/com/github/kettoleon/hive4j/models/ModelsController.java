package com.github.kettoleon.hive4j.models;

import com.github.kettoleon.hive4j.backend.Backend;
import com.github.kettoleon.hive4j.model.Model;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@Slf4j
public class ModelsController {

    @Autowired
    private List<Backend> backends;

    @Autowired
    private ModelPullHandler modelPullHandler;

    @Autowired
    private ModelAliasesRepository modelAliases;

    @GetMapping(path = {"/models"})
    public ModelAndView models() {
        //TODO add the ones that are still downloading...
        return page("models/models", "Models")
                .addObject("backends", backends)
                .addObject("modelAliases", modelAliases);

    }

    @PostMapping(path = {"/api/v1/backends/{backendId}/models"})
    public ModelAndView pullModel(@PathVariable("backendId") String backendId, @RequestParam("modelTag") String modelTag) {
        Backend backend = getBackend(backendId);

        Model model = backend.getModel(modelTag);

        if (backend.isModelPullingCapable()) {
            model.setPullProgressId(UUID.randomUUID().toString());
            modelPullHandler.addModelPull(model, backend.pullModel(model));
        }

        return new ModelAndView("models/model-cards")
                .addObject("models", backend.getAvailableModels())
                .addObject("backend", backend)
                .addObject("modelAliases", modelAliases);
    }

    @DeleteMapping(path = {"/api/v1/backends/{backendId}/models/{modelTag}"})
    public ModelAndView deleteModel(@PathVariable("backendId") String backendId, @PathVariable("modelTag") String modelTag) {
        Backend backend = getBackend(backendId);

        Model model = backend.getModel(modelTag);

        if (backend.isModelDeletingCapable()) {
            backend.deleteModel(model);
            modelAliases.deleteAll(modelAliases.findAllByBackendIdAndBackendModelId(backendId, modelTag));
        }

        return new ModelAndView("models/model-cards")
                .addObject("models", backend.getAvailableModels())
                .addObject("backend", backend)
                .addObject("modelAliases", modelAliases);
    }


    @PostMapping(path = {"/api/v1/backends/{backendId}/models/{modelTag}/aliases"})
    public ModelAndView setModelAliases(@PathVariable("backendId") String backendId, @PathVariable("modelTag") String modelTag, @RequestParam("aliases") String aliases) {

        Backend backend = getBackend(backendId);

        Model model = backend.getModel(modelTag);

        List<ModelAlias> currentAliases = modelAliases.findAllByBackendIdAndBackendModelId(backendId, modelTag);

        List<String> newAliases = Arrays.stream(aliases.split(",")).map(String::trim).collect(Collectors.toList());

        for(ModelAlias currentAlias: currentAliases){
            if(!newAliases.contains(currentAlias.getAlias())){
                modelAliases.delete(currentAlias);
            }
        }

        for(String newAlias : newAliases){
            if(currentAliases.stream().noneMatch(a -> a.getAlias().equals(newAlias))){
                ModelAlias modelAlias = new ModelAlias();
                modelAlias.setId(UUID.randomUUID().toString());
                modelAlias.setBackendId(backendId);
                modelAlias.setBackendModelId(modelTag);
                modelAlias.setAlias(newAlias);
                modelAliases.save(modelAlias);
            }
        }

        return new ModelAndView("models/model-card")
                .addObject("model", model)
                .addObject("backend", backend)
                .addObject("modelAliases", modelAliases);
    }



    private Backend getBackend(String backendId) {
        return backends.stream()
                .filter(b -> b.getId().equals(backendId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Backend not found"));
    }


}
