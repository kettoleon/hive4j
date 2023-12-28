package com.github.kettoleon.hive4j.functions.experimental;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskRouterFunction {

    @Autowired(required = false)
    private Model logicModel;

    String route(String prompt){
        //TODO maybe have different tasks register themselves instead of hardcoding them here
        //TODO make it learn from mistakes while routing

        String routing = logicModel.generate(Instruction.builder()
                        .system("""
                                You are part of an AI system that evaluates prompts to route them to different functions.
                                You will not answer any prompts, you will only answer with an evaluation of the type of task.
                                When in doubt, assume the prompts are meant for chatting with the agent by default.
                                Here is a list of available tasks:
                                  - chat: (default) continue chatting with the AI.
                                  - research: When asked to research a topic on the internet
                                  - learn: When asked to read a particular article, or watch a video and learn from it. Usually an url is provided.
                                You must refrain to make up new tasks, you can only use the ones provided above.
                                Provide the answer in the following json format: {"task": "taskId", "notes": "reasoning, doubts, clarifications or explanations"}
                                """)
                        .prompt(prompt)
                .build()).blockLast().getFullResponse();

        try {
            log.info("Routing task from:\n{}\n\nto:\n{}", prompt, routing);
            RoutingResponse resp = new ObjectMapper().readValue(routing, RoutingResponse.class);
            return resp.getTask();
        } catch (JsonProcessingException e) {
            log.warn("Error parsing response into json: {}", routing, e);
        }


       return "chat";
    }

    @Data
    @NoArgsConstructor
    private static class RoutingResponse {
        private String task;
        private String notes;
    }

}
