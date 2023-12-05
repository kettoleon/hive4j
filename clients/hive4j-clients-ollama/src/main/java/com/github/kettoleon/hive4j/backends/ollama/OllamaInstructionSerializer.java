package com.github.kettoleon.hive4j.backends.ollama;

import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.InstructionSerializer;

import java.util.List;
import java.util.Map;

public class OllamaInstructionSerializer implements InstructionSerializer {

    private final Map<String, InstructionSerializer> serializers = Map.ofEntries(
            Map.entry("orca2", new Orca2InstructionSerializer())
    );
    private final OLlamaModel model;

    public OllamaInstructionSerializer(OLlamaModel model) {
        this.model = model;
    }

    @Override
    public String serialize(List<Instruction> instruction) {
        return serializers.getOrDefault(model.getFamily(), i -> {
            throw new RuntimeException("No serializer found for model family: " + model.getFamily());
        }).serialize(instruction);
    }
}
