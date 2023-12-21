package com.github.kettoleon.hive4j.model;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @deprecated Use {@link Model} from a {@link com.github.kettoleon.hive4j.backend.Backend} instead
 */
@Deprecated
public interface ConfiguredLlmModel {

    int getTokenLimit();

    double[] embeddings(String text);

    double[] embeddings(Instruction instruction);

    int countTokens(Instruction instruction);

    int countTokens(String text);
    Flux<String> instruct(Instruction... instruction);

    Flux<String> instruct(List<Instruction> instruction);


}
