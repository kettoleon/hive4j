package com.github.kettoleon.hive4j.agents.repo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import static com.github.kettoleon.hive4j.model.Model.toSafeCssIdentifier;

@Entity
@Data
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

}
