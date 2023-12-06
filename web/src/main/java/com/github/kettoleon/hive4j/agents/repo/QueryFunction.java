package com.github.kettoleon.hive4j.agents.repo;

import com.github.kettoleon.hive4j.agent.Perspective;
import com.github.kettoleon.hive4j.agent.SwarmAgent;
import com.github.kettoleon.hive4j.agent.impl.SwarmAgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class QueryFunction {

    @Autowired
    private Model logicModel;

    @Autowired
    private SwarmAgentSystemPromptBuilder swarmAgentSystemPromptBuilder;

    public Flux<String> execute(Query query) {

        //TODO obviously this will do more things, but that is a start.

        return logicModel.generate(toInstruction(query));

    }

    private Instruction toInstruction(Query query) {
        SwarmAgent agent = query.getAgent().asApiAgent();

        return Instruction.builder()
                .system(buildSystemPromptForIntrospection(agent))
                .prompt(query.getQuery())
                .build();
    }

    private String buildSystemPromptForIntrospection(SwarmAgent agent) {
        String s = swarmAgentSystemPromptBuilder.buildSystemPrompt(agent, Perspective.SECOND_PERSON);
        s += "\nYou are being questioned by a system administrator or developer. Answer in the most introspective way possible.";
        return s;
    }

}
