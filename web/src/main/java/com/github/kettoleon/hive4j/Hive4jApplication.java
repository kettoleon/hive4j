package com.github.kettoleon.hive4j;

import com.github.kettoleon.hive4j.adapters.llama2.misc.Llama2InstructionSerializer;
import com.github.kettoleon.hive4j.agent.AgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.agent.impl.SwarmAgentSystemPromptBuilder;
import com.github.kettoleon.hive4j.backend.Backend;
import com.github.kettoleon.hive4j.backend.BackendFactory;
import com.github.kettoleon.hive4j.backends.BackendConfiguration;
import com.github.kettoleon.hive4j.backends.ConfiguredBackends;
import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.models.ollama.OLlamaConfiguredModel;
import com.github.kettoleon.hive4j.clients.ollama.model.OLlamaModel;
import com.github.kettoleon.hive4j.model.ConfiguredLlmModel;
import com.github.kettoleon.hive4j.simulation.RealTimeSimulationTimeProvider;
import com.github.kettoleon.hive4j.simulation.SimulationTimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
@Configuration
public class Hive4jApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Hive4jApplication.class).run(args);
    }

    @Bean
    public SimulationTimeProvider simulationTimeProvider() {
        return new RealTimeSimulationTimeProvider();
    }

    @Bean
    public AgentSystemPromptBuilder agentSystemPromptBuilder() {
        return new SwarmAgentSystemPromptBuilder();
    }

    @Bean
    public List<Backend> backends(ConfiguredBackends configuredBackends, Map<String, BackendFactory> backendFactories) {
        ArrayList<Backend> backends = new ArrayList<>();
        for (Map.Entry<String, BackendConfiguration> bc : configuredBackends.getBackends().entrySet()) {
            BackendConfiguration backendConfig = bc.getValue();
            BackendFactory backendFactory = backendFactories.get(backendConfig.getType() + BackendFactory.class.getSimpleName());
            backends.add(backendFactory.createBackend(bc.getKey(), backendConfig.getName(), backendConfig.getConfig()));
        }
        return backends;
    }

}
