package com.github.kettoleon.hive4j.clients.ollama.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLlamaModel {
    private String name;
    private long size;

    private String license;
    @JsonProperty("modelfile")
    private String modelFile;
    @JsonProperty("parameters")
    private String parameterString;

    private Map<String, List<String>> parameters;
    private String template;

    private OLlamaModelDetails details;

    @JsonIgnore
    public String getParameterString() {
        return parameterString;
    }


    public int getContextSize(){
        if(parameters == null || parameters.get("num_ctx") == null){
            return 0;
        }
        return Integer.parseInt(parameters.get("num_ctx").get(0));
    }

    public String getFamily(){
        return name.split(":")[0];
    }

    public String getTag(){
        return name.split(":")[1];
    }
}
