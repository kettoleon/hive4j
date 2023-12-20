package com.github.kettoleon.hive4j.clients.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kettoleon.hive4j.clients.ollama.model.*;
import com.github.kettoleon.hive4j.queue.FIFOLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpMethod.DELETE;

@Slf4j
public class OLlamaClient {

    private final WebClient webClient;

    private final FIFOLock generateLock = new FIFOLock();

    public OLlamaClient(String ollamaUrl) {
        webClient = WebClient.builder()
                .baseUrl(ollamaUrl)
                .build();
    }

    public String getVersion() {
        return webClient.get()
                .uri("/api/version")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(VersionResponse.class)
                .block()
                .getVersion();
    }

    public List<OLlamaModel> getModelList() {
        List<OLlamaModel> models = webClient.get()
                .uri("/api/tags")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ModelsResponse.class)
                .block()
                .getModels();
        return fillAdditionalInfo(models);
    }

    private List<OLlamaModel> fillAdditionalInfo(List<OLlamaModel> models) {
        for (OLlamaModel model : models) {
            OLlamaModel details = webClient.post()
                    .uri("/api/show")
                    .bodyValue(new ModelRequest(model.getName()))
                    .retrieve()
                    .bodyToMono(OLlamaModel.class)
                    .block();
            model.setLicense(details.getLicense());
            model.setModelFile(details.getModelFile());
            model.setParameters(mapParameters(details.getParameterString()));
            model.setTemplate(details.getTemplate());
        }
        return models;
    }

    private static Map<String, List<String>> mapParameters(String paramString) {
        HashMap<String, List<String>> parameters = new HashMap<>();

        for (String paramStr : paramString.split("\n")) {
            String[] split = paramStr.split("\s+");
            String key = split[0];
            String value = split[1];
            parameters.computeIfAbsent(key, k -> new ArrayList<>());
            parameters.get(key).add(value);
        }

        return parameters;
    }

    public Flux<DownloadStatus> pullModel(String modelName) {

        return toJsonObjectFlux(webClient.post()
                .uri("/api/pull")
                .bodyValue(new ModelRequest(modelName))
                .retrieve()
                .bodyToFlux(String.class), DownloadStatus.class);
    }

    public void deleteModel(String modelName) {
        webClient.method(DELETE)
                .uri("/api/delete")
                .bodyValue(new ModelRequest(modelName))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public Flux<String> generate(String modelName, String prompt, Profile... profiles) {
        GenerateRequest.GenerateRequestBuilder request = GenerateRequest.builder();
        request.model(modelName);
        request.prompt(prompt);
        request.raw(true);
        request.stream(true);
        if (profiles != null && profiles.length > 0) {
            Options.OptionsBuilder options = Options.builder();
            for (Profile profile : profiles) {
                profile.apply(options);
            }
            request.options(options.build());
        }

        return generate(request.build());
    }

    public Flux<String> generate(GenerateRequest generateRequest) {
        generateLock.await();
        return toJsonObjectFlux(webClient.post()
                .uri("/api/generate")
                .bodyValue(generateRequest)
                .retrieve()
                .bodyToFlux(String.class)
                .doFinally(signalType -> generateLock.done()), GenerateResponse.class)
                .map(GenerateResponse::getResponse);

    }

    public double[] embeddings(String modelName, String prompt) {
        return webClient.post()
                .uri("/api/embeddings")
                .bodyValue(new EmbeddingsRequest(modelName, prompt))
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block().getEmbedding();
    }

    private static <T> Flux<T> toJsonObjectFlux(Flux<String> response, Class<T> valueType) {
        return response.flatMap(s -> Flux.fromArray(s.split("\n")))
                .map(jsonString -> {
                    try {
                        return new ObjectMapper().readValue(jsonString, valueType);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
