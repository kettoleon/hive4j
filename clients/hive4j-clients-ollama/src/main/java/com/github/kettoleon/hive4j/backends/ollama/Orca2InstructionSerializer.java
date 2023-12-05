package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.InstructionSerializer;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Orca2InstructionSerializer implements InstructionSerializer {


    private static final String START_TK = "<|im_start|>";
    private static final String END_TK = "<|im_end|>\n";
    private static final String SYSTEM_TEMPLATE = START_TK + "system\n%s" + END_TK;
    private static final String PROMPT_TEMPLATE = START_TK + "user\n%s" + END_TK;
    private static final String ASSISTANT_TEMPLATE = START_TK + "assistant\n%s";

    @Override
    public String serialize(List<Instruction> instructions) {
        return instructions.stream().map(this::serialize).collect(Collectors.joining(END_TK));
    }

    public String serialize(Instruction instruction) {
        StringBuilder sb = new StringBuilder();
        if (isNotBlank(instruction.getSystem())) {
            sb.append(String.format(SYSTEM_TEMPLATE, instruction.getSystem()));
        }
        sb.append(String.format(PROMPT_TEMPLATE, instruction.getPrompt()));
        sb.append(String.format(ASSISTANT_TEMPLATE, instruction.getResponseOrForcedResponseStart()));
        return sb.toString();
    }

}
