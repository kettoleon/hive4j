package com.github.kettoleon.hive4j.functions.experimental;

import com.github.kettoleon.hive4j.agent.Perspective;
import com.github.kettoleon.hive4j.agent.SwarmAgent;
import com.github.kettoleon.hive4j.agent.impl.SwarmAgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agents.repo.Query;
import com.github.kettoleon.hive4j.model.GenerateProgress;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Component
public class QueryFunction {

    @Autowired(required = false)
    private Model logicModel;

    @Autowired
    private PromptEnhancerFunction promptEnhancerFunction;
    @Autowired
    private ResearchFunction researchFunction;

    @Autowired
    private TaskRouterFunction taskRouterFunction;

    @Autowired
    private SwarmAgentSystemPromptBuilder swarmAgentSystemPromptBuilder;

    public Flux<GenerateProgress> execute(Query query) {

        if (logicModel == null) {
            return Flux.just(GenerateProgress.builder().errorMessage("Sorry, no logicModel bean available in the context.").build()).delayElements(Duration.ofMillis(1000));
        }

        String enhancedPrompt = promptEnhancerFunction.enhance(query.getQuery());
        query.setQuery(enhancedPrompt);

        //TODO we need the context of the conversation and the agent first, just in case we say reasearch "this" or "go and research whatever you want"
        //TODO so we need to have chat implemented first.
        String route = taskRouterFunction.route(query.getQuery());
        switch (route) {

            case "research":
                researchFunction.research(query.getQuery(), query.getAgent().asApiAgent());
                return Flux.just(GenerateProgress.builder().errorMessage("I've searched a few things on the internet and learnt from it.").build()).delayElements(Duration.ofMillis(1000));
            case "learn":
                return Flux.just(GenerateProgress.builder().errorMessage("I'll learn from the resource and get back to you").build()).delayElements(Duration.ofMillis(1000));
            default:
                return logicModel.generate(toInstruction(query));
        }


    }

    private Instruction toInstruction(Query query) {
        SwarmAgent agent = query.getAgent().asApiAgent();

        return Instruction.builder()
                .system(buildSystemPromptForIntrospection(agent))
                .prompt(query.getQuery())
                .build();
    }

    private String buildSystemPromptForIntrospection(SwarmAgent agent) {
        //NOTE: For some reason orca2 places the whole answer in a single line when asked to use markdown format...
        //NOTE: The more we asked orca2 to use markdown characteristics, the more it went back to not using line breaks again...
        //NOTE: then we tried using HTML instead, but it did not work well in all cases either...
        //NOTE: so we decided to just let the model answer in its preferred format, which is close to markdown.
        String s = swarmAgentSystemPromptBuilder.buildSystemPrompt(agent, Perspective.SECOND_PERSON);
        s += "\nYou are being questioned by a system administrator or developer. Answer in the most introspective way possible.";
//        s += "\nYou MUST use basic HTML to format your answers.";
//        s += "\nYou MUST use a pre html tag for code blocks or snippets.";
//        s += "\nHere are some instructions for formatting your answers:";
//        s += "\nYou must use a hash before text for headings or titles. Add more hashes for nesting headers. For example:";
//        s += "\n# Heading level 1";
//        s += "\n## Subheading";
//        s += "\nYou can use **text** for bold text";
//        s += "\nYou can use ~~text~~ for strikethrough text";
//        s += "\nYou can use *text* for italics";
//        s += "\nYou can start a line with \">\" for quotes"; //Not even like this the model learned to use them
//        s += "\nYou can use 1. 2. 3. for numbered lists"; //The model already does numbered and unordered lists well
//        s += "\nYou can use three hyphens --- for an horizontal rule";
//        s += "\nYou must use `text` for inline code";
//        s += "\nYou must use ```text``` for code blocks or snippets";
        return s;
    }

}
