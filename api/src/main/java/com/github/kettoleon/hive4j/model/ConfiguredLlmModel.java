package com.github.kettoleon.hive4j.model;

import reactor.core.publisher.Flux;

import java.util.List;

public interface ConfiguredLlmModel {

    int getTokenLimit();

    double[] embeddings(String text);

    double[] embeddings(Instruction instruction);

    int countTokens(Instruction instruction);

    int countTokens(String text);
    Flux<String> instruct(Instruction... instruction);

    Flux<String> instruct(List<Instruction> instruction);


}
