package com.github.kettoleon.hive4j.agents.repo;

import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class QueryFunction {

    @Autowired
    private Model logicModel;

    public Flux<String> execute(Query query) {

        //TODO obviously this will do more things, but that is a start.

        return logicModel.generate(toInstruction(query));

    }

    private Instruction toInstruction(Query query) {
        return Instruction.builder()
                .prompt(query.getQuery())
                .build();
    }

}
