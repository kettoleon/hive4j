package com.github.kettoleon.hive4j.agents.repo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
