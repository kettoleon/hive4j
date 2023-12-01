package com.github.kettoleon.hive4j.adapters.llama2.function;

import com.github.kettoleon.hive4j.agent.Agent;
import com.github.kettoleon.hive4j.agent.AgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import com.github.kettoleon.hive4j.agent.Perspective;
import com.github.kettoleon.hive4j.function.ChatFunction;
import com.github.kettoleon.hive4j.memory.AgentMemoryRepository;
import com.github.kettoleon.hive4j.memory.MemoryStream;
import com.github.kettoleon.hive4j.memory.MemoryType;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.simulation.SimulationTimeProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.adapters.llama2.misc.DateTimeFormats.MODEL_READABLE_FORMAT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@RequiredArgsConstructor
public class ChatFunctionLlama2Strategy implements ChatFunction {
    private final ConfiguredLlmModel model;

    private final SimulationTimeProvider simulationTimeProvider;
    private final CommonLlama2InstructionBuilder commonInstructions;
//    private final AgentMemoryRepository agentMemoryRepository;

//    private final ReflectionFunction reflectionFunction;

    private final AgentSystemPromptBuilder promptBuilder;

    private Map<String, List<Instruction>> messages = new HashMap<>();
    private Map<String, Instruction> systemMessages = new HashMap<>();

    @Override
    public void loadMessage(String chatName, ZonedDateTime time, Agent from, String msg) {
        if (!systemMessages.containsKey(chatName)) {
            systemMessages.put(chatName, buildSystemMessage(time, (GenerativeAgent) from));
        }
        getOrCreateMessages(chatName).add(Instruction.builder()
                .instruction(from.getName() + ": " + msg)
                .build());
    }

    @Override
    public Flux<String> join(String chatName, GenerativeAgent agent, List<Agent> interlocutors) {

        ZonedDateTime time = simulationTimeProvider.getCurrentTime();
        Instruction system = buildSystemMessage(time, agent);
        systemMessages.put(chatName, system);

        Instruction joined = Instruction.builder()
                .instruction(agent.getName() + " just joined " + chatName + " with " + interlocutors.stream().map(Agent::getName).collect(Collectors.joining(", ")))
                .build();
        getOrCreateMessages(chatName).add(joined);

//        commonInstructions.loadAgentContextIntoSystemInstruction(system, agentMemoryRepository.getMemoryStream(agent).getMemoriesByCreatedDesc(), joined);

        StringBuilder sb = new StringBuilder();

        return model.instruct(system, joined)
                .doOnNext(sb::append)
                .doFinally(signalType -> {
                    String responseString = sb.toString();
                    if (isNotBlank(responseString)) {
                        joined.setForcedResponseStart(responseString);
                    }
                });
    }

    private Instruction buildSystemMessage(ZonedDateTime time, GenerativeAgent agent) {
        Instruction system = Instruction.builder()
                .system(String.format("""
                                Current time is %s.
                                %s
                                You can answer with system commands instead of text if it wants to perform an action.
                                Use /noop if you don't want to do or say anything.
                                Use /suggestion <message> if you would like to do an action that has not been implemented yet.
                                Use /close when you consider the conversation has ended.
                                Never send commands after or between text. Either send a command or text.
                                Never mention those commands to anyone.
                                """, time.format(MODEL_READABLE_FORMAT),
                        promptBuilder.buildSystemPrompt(agent, Perspective.SECOND_PERSON)
                ))
                .instruction("Here are part of your memories, use them as context.")
                .forcedResponseStart("Understood.")
                .build();
        return system;
    }

    private String trimResponse(String msg) {
        if (msg.startsWith("<s>")) {
            msg = StringUtils.substringAfter(msg, "<s>");
        }
        if (msg.endsWith("</s>")) {
            msg = StringUtils.substringBeforeLast(msg, "</s>");
        }
        msg = msg.trim();
        if (msg.startsWith("\"") && msg.endsWith("\"")) {
            msg = msg.substring(1, msg.length() - 1);
        }
        msg = msg.replaceAll("<\\/*s>", "");
        return msg;
    }

    private List<Instruction> getOrCreateMessages(String chatName) {
        return messages.computeIfAbsent(chatName, (e) -> new ArrayList<>());
    }

    @Override
    public Flux<String> addMessage(String chatName, Agent from, GenerativeAgent to, String msg) {
        Instruction instruction = Instruction.builder()
                .instruction(from.getName() + ": " + msg)
                .build();
        getOrCreateMessages(chatName).add(instruction);

//        commonInstructions.loadAgentContextIntoSystemInstruction(systemMessages.get(chatName), agentMemoryRepository.getMemoryStream(to).getMemoriesByCreatedDesc(), getOrCreateMessages(chatName));

        StringBuilder sb = new StringBuilder();

        return model.instruct(getSystemAndMessageInstructions(chatName))
                .doOnNext(sb::append)
                .doFinally(signalType -> {
                    String responseString = sb.toString();
                    if (isNotBlank(responseString)) {
                        instruction.setForcedResponseStart(responseString);
                    }
                });
    }

    private List<Instruction> getSystemAndMessageInstructions(String chatName) {
        ArrayList<Instruction> instructions = new ArrayList<>();
        instructions.add(systemMessages.get(chatName));
        instructions.addAll(getOrCreateMessages(chatName));
        return instructions;
    }

    @Override
    public Flux<String> close(String chatName, GenerativeAgent agent) {
        Instruction instruction = Instruction.builder()
                .instruction("The conversation is being closed. Take a deep breath and provide a summary of what has been discussed, make sure to include agreements, decisions taken and relevant insights for you.")
                .forcedResponseStart("Had a conversation with ")
                .build();
        getOrCreateMessages(chatName).add(instruction);

//        MemoryStream memoryStream = agentMemoryRepository.getMemoryStream(agent);
//        commonInstructions.loadAgentContextIntoSystemInstruction(systemMessages.get(chatName), memoryStream.getMemoriesByCreatedDesc(), getOrCreateMessages(chatName));

// TODO        reflectionFunction.reflect(agent);

        StringBuilder sb = new StringBuilder();

        return model.instruct(getSystemAndMessageInstructions(chatName))
                .doOnNext(sb::append)
                .doFinally(signalType -> {
                    String responseString = sb.toString();
//                    if (isNotBlank(responseString)) {
//                        memoryStream.addMemory(MemoryType.OBSERVATION, responseString);
//                    }
                });
    }

    @Override
    public void clearConversation(String chatName) {
        messages.remove(chatName);
        systemMessages.remove(chatName);
    }
}
