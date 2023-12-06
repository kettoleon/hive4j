package com.github.kettoleon.hive4j.agents.repo;

import com.github.kettoleon.hive4j.agent.SwarmAgent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.github.kettoleon.hive4j.model.Model.toSafeCssIdentifier;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hive4jSwarmAgent {

    @Id
    private String id;

    private String name;

    private List<String> statements;
    private List<String> traits;

    private String specialty;
    private String mission;
    private String function;

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY, orphanRemoval = true, cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    private List<Query> queries;

    public String getHtmlId() {
        return toSafeCssIdentifier(id);
    }

    public String getStatementsInNewLines() {
        return String.join("\n", statements);
    }

    public String getCommaSeparatedTraits() {
        return String.join(", ", traits);
    }

    public SwarmAgent asApiAgent() {
        SwarmAgent agent = new SwarmAgent();
        agent.setId(id);
        agent.setName(name);
        agent.setStatements(statements);
        agent.setTraits(traits);
        agent.setSpecialty(specialty);
        agent.setMission(mission);
        agent.setFunction(function);
        return agent;
    }
}
