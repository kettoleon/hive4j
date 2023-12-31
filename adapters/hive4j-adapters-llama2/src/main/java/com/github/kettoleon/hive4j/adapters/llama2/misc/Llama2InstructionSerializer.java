package com.github.kettoleon.hive4j.adapters.llama2.misc;


import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.InstructionSerializer;

import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Llama2InstructionSerializer implements InstructionSerializer {

    public String serialize(List<Instruction> instruction) {
        return instruction.stream().map(this::serialize).collect(joining("</s>\n"));
    }

    public String serialize(Instruction instruction) {
        return String.format("<s>[INST] %s [/INST]\n%s", systemPromptWithUserMessage(instruction), isNotBlank(instruction.getForcedResponseStart()) ? instruction.getForcedResponseStart() : "");
    }

    private String systemPromptWithUserMessage(Instruction instruction) {
        if (isNotBlank(instruction.getSystem())) {
            return String.format("""
                    <<SYS>>
                    %s
                    <</SYS>>
                    
                    %s""", instruction.getSystem(), instruction.getPrompt());
        }
        return instruction.getPrompt();
    }

}
