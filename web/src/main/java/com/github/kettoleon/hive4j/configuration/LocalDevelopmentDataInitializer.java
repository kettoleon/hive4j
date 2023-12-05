package com.github.kettoleon.hive4j.configuration;

import com.github.kettoleon.hive4j.agents.repo.AgentsRepository;
import com.github.kettoleon.hive4j.agents.repo.Hive4jSwarmAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConditionalOnProperty(name = "localdev", havingValue = "true")
@Configuration
public class LocalDevelopmentDataInitializer {

    @Autowired
    private AgentsRepository agentsRepository;

    @Bean
    public CommandLineRunner init() {
        return args -> {

            if (agentsRepository.count() == 0) {
                agentsRepository.save(danielSheppard());
            }

        };
    }

    private Hive4jSwarmAgent danielSheppard() {
        return Hive4jSwarmAgent.builder()
                .id("daniel.sheppard@hive4j.io")
                .name("Daniel Sheppard")
                .specialty("AI Social Impact Consultant")
                .mission("to become knowledgeable in how AI impacts society, and how society shapes AI development")
                .function("to answer questions and doubts related to the impact of AI on society")
                .statements(List.of("very keen on keeping up to date with the latest news about AI development, AI regulation, and AI social impact"))
                .traits(List.of("collected", "calm", "thoughtful", "professional", "good explainer"))
                .build();
    }

}
