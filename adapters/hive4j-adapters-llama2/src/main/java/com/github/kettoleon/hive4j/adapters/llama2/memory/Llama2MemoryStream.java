package com.github.kettoleon.hive4j.adapters.llama2.memory;

import com.github.kettoleon.hive4j.agent.GenerativeAgent;
import com.github.kettoleon.hive4j.function.MemoryRatingFunction;
import com.github.kettoleon.hive4j.memory.Memory;
import com.github.kettoleon.hive4j.memory.MemoryStream;
import com.github.kettoleon.hive4j.memory.MemoryType;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.simulation.SimulationTimeProvider;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.nd4j.linalg.factory.Nd4j;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class Llama2MemoryStream implements MemoryStream {

    private static final double RECENCY_WEIGHT = 1;
    private static final double RELEVANCE_WEIGHT = 1;
    private static final double IMPORTANCE_WEIGHT = 1;
    private static final double RECENCY_DECAY_FACTOR = 0.995;

    private final MemoryRatingFunction memoryRatingFunction;
    private final ConfiguredLlmModel llmModel;
    private final SimulationTimeProvider simulationTimeProvider;
    private final GenerativeAgent agent;

    private List<Memory> memories = new ArrayList<>();

    @Override
    public Memory addMemory(MemoryType type, String description) {
        Memory memory = Memory.builder()
                .id(UUID.randomUUID().toString())
                .created(simulationTimeProvider.getCurrentTime())
                .lastAccessed(simulationTimeProvider.getCurrentTime())
                .memoryType(type)
                .description(description)
                .importance(memoryRatingFunction.rateMemory(agent, description))
                .embeddingVector(llmModel.embeddings(description))
                .build();
        memories.add(memory);
        return memory;
    }

    @Override
    public void setMemories(List<Memory> memories) {
        this.memories = memories;
    }

    @Override
    public List<Memory> getMemories() {
        return memories;
    }

    @Override
    public List<Memory> getMemoriesByCreatedDesc() {
        return streamMemoriesByCreatedDesc().collect(toList());
    }

    private Stream<Memory> streamMemoriesByCreatedDesc() {
        return memories.stream().sorted(comparing(Memory::getCreated));
    }

    @Override
    public List<Memory> getMemoriesByRelevanceDesc(String query) {
        return streamMemoriesByRelevanceDesc(query).map(Pair::getRight).collect(toList());
    }

    private Stream<Pair<Double, Memory>> streamMemoriesByRelevanceDesc(String query) {
        double[] queryVector = llmModel.embeddings(query);

        return memories.stream().map(memory -> {
            double relevance = computeSimilarity(queryVector, memory.getEmbeddingVector());
            double score = computeImportanceWithRecency(memory) + RELEVANCE_WEIGHT * relevance;
            return Pair.of(score, memory);
        }).sorted(Comparator.comparingDouble(this::getScore).reversed());
    }

    private double getScore(Pair<Double, Memory> t) {
        return t.getLeft();
    }


    private double computeImportanceWithRecency(Memory memory) {
        double recency = exponentialDecay(Duration.between(memory.getLastAccessed(), simulationTimeProvider.getCurrentTime()).toHours());
        return RECENCY_WEIGHT * recency + IMPORTANCE_WEIGHT * memory.getImportance();
    }

    private double exponentialDecay(long hours) {
        return Math.pow(RECENCY_DECAY_FACTOR, hours);
    }

    @Override
    public List<Memory> findMemoriesByRelevance(String query, int limit, double threshold) {
        return streamMemoriesByRelevanceDesc(query).filter(memory -> memory.getLeft() > threshold).limit(limit).map(Pair::getRight).collect(toList());
    }

    @Override
    public List<Memory> findMemoriesByCreated(GenerativeAgent agent, int limit) {
        return streamMemoriesByCreatedDesc().limit(limit).collect(toList());
    }

    @Override
    public List<Memory> findMemoriesByTimePeriod(ZonedDateTime from, ZonedDateTime to) {
        return memories.stream().filter(memory -> memory.getCreated().isAfter(from) && memory.getCreated().isBefore(to)).collect(toList());
    }

    public double computeSimilarity(double[] vector1, double[] vector2) {
        double norm_a = 0.0;
        double norm_b = 0.0;
        //TODO Assuming both vectors have the same length
        for (int i = 0; i < vector1.length; i++) {
            norm_a += Math.pow(vector1[i], 2);
            norm_b += Math.pow(vector2[i], 2);
        }
        return (dotProduct(vector1, vector2) / (Math.sqrt(norm_a) * Math.sqrt(norm_b)));
    }

    private static double dotProduct(double[] vector1, double[] vector2) {
        return Nd4j.getBlasWrapper().dot(Nd4j.create(vector1), Nd4j.create(vector2));
    }
}
