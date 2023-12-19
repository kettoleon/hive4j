package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.InstructionSerializer;

import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LlamaInstructionSerializer implements InstructionSerializer {

    private static final String INT_START_TK = "<s>";
    private static final String INT_END_TK = "</s>\n";
    private static final String INSTRUCTION_TEMPLATE = "[INST]%s %s[/INST]\n";
    private static final String SYSTEM_TEMPLATE = " <<SYS>>%s<</SYS>>";

    @Override
    public String serialize(List<Instruction> instructions) {
        return instructions.stream().map(this::serialize).collect(Collectors.joining(INT_END_TK));
    }

    public String serialize(Instruction instruction) {
        StringBuilder sb = new StringBuilder();
        sb.append(INT_START_TK);
        String system = "";
        if (isNotBlank(instruction.getSystem())) {
            system = String.format(SYSTEM_TEMPLATE, instruction.getSystem());
        }
        sb.append(String.format(INSTRUCTION_TEMPLATE, system, instruction.getPrompt()));
        if(isNotBlank(instruction.getResponseOrForcedResponseStart())) {
            sb.append(instruction.getResponseOrForcedResponseStart());
        }
        return sb.toString();
    }
}
