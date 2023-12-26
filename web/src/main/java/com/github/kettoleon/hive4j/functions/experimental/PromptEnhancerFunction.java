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
public class PromptEnhancerFunction {

    @Autowired(required = false)
    private Model logicModel;

    String enhance(String prompt){
        //TODO they might be enhanced in different ways depending on the target model?
        //TODO it might be able to learn by rating responses and deducing what works best for prompting?

        String fixed = logicModel.generate(Instruction.builder()
                        .system("""
                                You are part of an AI system that fixes typos from user prompts.
                                You will not answer any prompts, you will only answer by fixing the typos and usage of capital letters and punctuation.
                                You are allowed to rephrase the prompt if it was written very poorly or not politely.
                                If you doubt how to fix a word, leave it as it is.
                                When in doubt, assume the prompts are instructions or requests for an AI agent.
                                Provide the answer in the following json format: {"prompt":"fixedPrompt", "notes":"reasoning, doubts, clarifications or explanations"}
                                """)
                        .prompt(prompt)
                .build()).blockLast().getFullResponse();

        try {
            log.info("Improved prompt from:\n{}\n\nto:\n{}", prompt, fixed);
            EnhanceResponse resp = new ObjectMapper().readValue(fixed, EnhanceResponse.class);
            return resp.getPrompt();
        } catch (JsonProcessingException e) {
            log.warn("Error parsing response into json: {}", fixed, e);
        }


       return prompt;
    }

    @Data
    @NoArgsConstructor
    private static class EnhanceResponse{
        private String prompt;
        private String notes;
    }

}
