package com.github.kettoleon.hive4j.adapters.llama2.function;

import com.github.kettoleon.hive4j.agent.AgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import com.github.kettoleon.hive4j.agent.Perspective;
import com.github.kettoleon.hive4j.function.MemoryRatingFunction;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.model.Instruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static com.github.kettoleon.hive4j.adapters.llama2.misc.StringUtils.parseIntOrZero;


@Component
@RequiredArgsConstructor
public class MemoryRatingLlama2Strategy implements MemoryRatingFunction {

    private final ConfiguredLlmModel model;
    private final AgentSystemPromptBuilder<GenerativeAgent> agentSystemPromptBuilder;

    @Override
    public double rateMemory(GenerativeAgent agent, String memory) {

        //TODO we don't check for token limits in this case, it does not seem like we will reach it for now at least...
        //TODO it might be useful in the future to fill the prompt token limit with most important background about the agent?

        Instruction memoryEvaluation = Instruction.builder()
                .system(agentSystemPromptBuilder.buildSystemPrompt(agent, Perspective.SECOND_PERSON))
                .instruction("""
                        On the scale of 1 to 10, where 1 is purely mundane (e.g., brushing teeth, making bed) and 10 is
                        extremely poignant (e.g., a break up, college acceptance), rate the likely poignancy of the
                        following piece of memory. You must answer with only a numeric value. Do not provide explanation
                        or description.
                        """)
                .input("Memory: " + memory)
                .build();

        String value = blockToString(model.instruct(memoryEvaluation));

        int poignancy = parseIntOrZero(value);
        if (poignancy == 0) {
            memoryEvaluation.setForcedResponseStart(value);
            Instruction insist = Instruction.builder()
                    .instruction("Please, we insist, provide an answer with only a numeric value. We need to parse it for another system.")
                    .forcedResponseStart("Rating: ")
                    .build();
            value = blockToString(model.instruct(memoryEvaluation, insist));
            poignancy = parseIntOrZero(value);
            //TODO collection of failed attempts into a report for later reviewing and improving the system? (Without annoying logs ofc)
//            if (poignancy == 0) {
//                System.err.println("Incorrect response: " + value);
//                System.err.println("(Failed to evaluate memory: " + memory + ")");
//            }
        }
//        if (poignancy > 0) {
//            System.out.println("P: " + poignancy + " M: " + memory);
//        }
        return poignancy/10.0;
    }

    public static String blockToString(Flux<String> instruct) {
        return instruct.collectList().map(list -> String.join("", list)).block();
    }


}
