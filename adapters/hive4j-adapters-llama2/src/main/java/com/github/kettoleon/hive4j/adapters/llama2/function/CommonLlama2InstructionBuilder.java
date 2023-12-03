package com.github.kettoleon.hive4j.adapters.llama2.function;

import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import com.github.kettoleon.hive4j.memory.Memory;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.model.Instruction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//@Component
@RequiredArgsConstructor
@Slf4j
public class CommonLlama2InstructionBuilder {

    private static final double RESERVED_FOR_ANSWER_FACTOR_INVERSE = 0.85; //This leaves around 4096 = 3482 (prompt) + 614 (Response) (0.9 was only 400 tokens response and proved too small for planning)

    private final ConfiguredLlmModel model;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Instruction buildAgentBackgroundInstruction(GenerativeAgent agent) {
        Instruction background = Instruction.builder()
                .instruction("Here is some background and context about a fictional character. For now, only understand and remember it. We will ask you follow-up questions later.")
                .input(generateBackgroundInput(agent))
                .forcedResponseStart("Understood. I'm ready to answer questions taking this background and context into account.")
                .build();
        return background;
    }

    public Instruction buildAgentMemoryContextInstructionFromQueryResultFillingTokenLimit(Iterator<Pair<Double, Memory>> queryResult, Instruction... additionalPromptInstructions) {
        return buildAgentMemoryContextInstructionFromQueryResult(queryResult, calculateRemainingTokens(additionalPromptInstructions));
    }

    public Instruction buildAgentMemoryContextInstructionFromQueryResult(Iterator<Pair<Double, Memory>> relevantMemories, final int remainingTokens) {


        Instruction contextInstruction = getContextInstruction();
        setContextInstructionInput(new Iterator<>() {

            @Override
            public boolean hasNext() {
                return relevantMemories.hasNext();
            }

            @Override
            public Memory next() {
                return relevantMemories.next().getRight();
            }
        }, remainingTokens, contextInstruction);
        return contextInstruction;
    }

    private int calculateRemainingTokens(Instruction... instructions) {
        return calculateRemainingTokens(Arrays.stream(instructions).toList());
    }

    private int calculateRemainingTokens(List<Instruction> instructions) {
        return getTokenLimitMinusReservedSpaceForAnswer() - instructions.stream().mapToInt(model::countTokens).sum();
    }

    private int getTokenLimitMinusReservedSpaceForAnswer() {
        return (int) (model.getTokenLimit() * RESERVED_FOR_ANSWER_FACTOR_INVERSE);
    }

    public void loadAgentContextIntoSystemInstruction(Instruction system, List<Memory> memories, List<Instruction> additionalPromptInstructions) {
        setContextInstructionInput(memories.listIterator(), calculateRemainingTokens(additionalPromptInstructions), system);
    }
    public void loadAgentContextIntoSystemInstruction(Instruction system, List<Memory> memories, Instruction... additionalPromptInstructions) {
        loadAgentContextIntoSystemInstruction(system, memories, Arrays.stream(additionalPromptInstructions).toList());
    }

    public Instruction buildAgentMemoryContextInstructionFillingTokenLimit(List<Memory> memories, Instruction... additionalPromptInstructions) {
        Instruction contextInstruction = getContextInstruction();
        setContextInstructionInput(memories.listIterator(), calculateRemainingTokens(additionalPromptInstructions), contextInstruction);
        return contextInstruction;
    }

    public Instruction setContextInstructionInput(List<Memory> memories, final int remainingTokens) {
        Instruction contextInstruction = getContextInstruction();
        setContextInstructionInput(memories.listIterator(), remainingTokens, contextInstruction);
        return contextInstruction;
    }

    private static Instruction getContextInstruction() {
        return Instruction.builder()
                .instruction("Here are some of the agent's memories relevant for the task at hand. For now, only understand and remember them. We will ask you follow-up questions later.")
                .input("#")
                .forcedResponseStart("Understood. I'm ready to answer questions taking these character memories into account.")
                .build();
    }

    private void setContextInstructionInput(Iterator<Memory> memories, final int remainingTokens, Instruction contextInstruction) {

        int baseTokens = model.countTokens(contextInstruction);
        int tokensCountdown = remainingTokens - baseTokens;

        contextInstruction.setInput(generateMemoriesInput(memories, tokensCountdown));

    }

    private String generateMemoriesInput(Iterator<Memory> memories, int tokensCountdown) {
        StringBuilder sb = new StringBuilder();
        while (memories.hasNext()) {
            Memory m = memories.next();
            String memoryDescription = serializeMemory(m);
            int currentMemTokens = model.countTokens(memoryDescription);
            if (currentMemTokens > tokensCountdown) {
                break;
            }
            sb.append(memoryDescription);
            tokensCountdown -= currentMemTokens;
        }
        return sb.toString();
    }

    private String serializeMemory(Memory m) {
        StringBuilder sb = new StringBuilder();
        sb.append(m.getCreated().format(DATE_TIME_FORMATTER));
        sb.append(" [");
        sb.append(m.getMemoryType().name());
        sb.append("] ");
        sb.append(m.getDescription());
        sb.append("\n");
        return sb.toString();
    }

    private String generateBackgroundInput(GenerativeAgent agent) {
        throw new UnsupportedOperationException("Need to use the new system prompt generators instead");
//        StringBuilder sb = new StringBuilder();
//        sb.append("Background: " + agent.getName() );
//        sb.append(agent.getDescription());
//        sb.append("\n");
//        sb.append("Traits: " + agent.getName() + " " + agent.getTraits());
//        sb.append("\n");
//        if (StringUtils.isNotBlank(agent.getSummary())) {
//            sb.append("Summary: ");
//            sb.append(agent.getSummary());
//            sb.append("\n");
//        }
//        return sb.toString();
    }
}
