package com.github.kettoleon.hive4j.clients.ollama;

import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.InstructionSerializer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

@Slf4j
public class OLlamaConfiguredModel implements ConfiguredLlmModel {

    private final OLlamaClient client;
    private final OLlamaModel model;
    private final InstructionSerializer serializer;
    private final int tokenLimit;

    public OLlamaConfiguredModel(OLlamaClient client, OLlamaModel model, InstructionSerializer serializer) {
        this.client = client;
        this.model = model;
        this.tokenLimit = Integer.parseInt(model.getParameters().get("num_ctx").get(0));
        this.serializer = serializer;
    }

    @Override
    public int getTokenLimit() {
        return tokenLimit;
    }

    @Override
    public double[] embeddings(String text) {
        return client.embeddings(model.getName(), text);
    }

    @Override
    public double[] embeddings(Instruction instruction) {
        return embeddings(serializer.serialize(instruction));
    }

    @Override
    public int countTokens(Instruction instruction) {
        return countTokens(serializer.serialize(instruction));
    }

    @Override
    public int countTokens(String text) {
        double[] embeddings = embeddings(text);
        if(embeddings == null){
            log.warn("Embeddings for text: {} are null", text);
            return 0;
        }
        return embeddings.length;
    }

    @Override
    public Flux<String> instruct(Instruction... instruction) {
        return instruct(asList(instruction));
    }

    @Override
    public Flux<String> instruct(List<Instruction> instruction) {
        return client.generate(model.getName(), instruction.stream().map(serializer::serialize).collect(joining("</s>\n")));
    }
}
