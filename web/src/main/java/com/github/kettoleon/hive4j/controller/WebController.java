package com.github.kettoleon.hive4j.controller;

import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.clients.ollama.model.DownloadStatus;
import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;

@Controller
@Slf4j
public class WebController {


    @Autowired
    private OLlamaClient oLlamaClient;

    @GetMapping(path = {"", "/", "/home"})
    public ModelAndView dashboard() {
        return page("home", "Home");
    }

    @GetMapping(path = {"/models"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<OLlamaModel> models() {
        return oLlamaClient.getModelList();
    }

    @GetMapping(path = {"/models/{modelId}/pull"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Flux<DownloadStatus> pullModel(@PathVariable("modelId") String modelName) {
        log.info("Pulling model {}", modelName);
        return oLlamaClient.pullModel(modelName);
    }


}
