package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Options {

    @JsonProperty("num_keep")
    private int numKeep;
    @JsonProperty("seed")
    private int seed;
    @JsonProperty("num_predict")
    private int num_predict;
    @JsonProperty("top_k")
    private int topK;
    @JsonProperty("top_p")
    private double topP;
    @JsonProperty("tfs_z")
    private double tfsZ;
    @JsonProperty("typical_p")
    private double typicalP;
    @JsonProperty("repeat_last_n")
    private int repeatLastN;
    @JsonProperty("temperature")
    private double temperature;
    @JsonProperty("repeat_penalty")
    private double repeatPenalty;
    @JsonProperty("presence_penalty")
    private double presencePenalty;
    @JsonProperty("frequency_penalty")
    private double frequencyPenalty;
    @JsonProperty("mirostat")
    private int microstat;
    @JsonProperty("mirostat_tau")
    private double microstatTau;
    @JsonProperty("mirostat_eta")
    private double microstatEta;
    @JsonProperty("penalize_newline")
    private boolean penalizeNewline;
    @JsonProperty("stop")
    private List<String> stop;
    @JsonProperty("numa")
    private boolean numa;
    @JsonProperty("num_ctx")
    private int numCtx;
    @JsonProperty("num_batch")
    private int numBatch;
    @JsonProperty("num_gqa")
    private int numGqa;
    @JsonProperty("num_gpu")
    private int numGpu;
    @JsonProperty("main_gpu")
    private int mainGpu;
    @JsonProperty("low_vram")
    private boolean lowVram;
    @JsonProperty("f16_kv")
    private boolean f16Kv;
    @JsonProperty("logits_all")
    private boolean logitsAll;
    @JsonProperty("vocab_only")
    private boolean vocabOnly;
    @JsonProperty("use_mmap")
    private boolean useMmap;
    @JsonProperty("use_mlock")
    private boolean useMlock;
    @JsonProperty("embedding_only")
    private boolean embeddingOnly;
    @JsonProperty("rope_frequency_base")
    private double ropeFrequencyBase;
    @JsonProperty("rope_frequency_scale")
    private double ropeFrequencyScale;
    @JsonProperty("num_thread")
    private int numThread;

}
