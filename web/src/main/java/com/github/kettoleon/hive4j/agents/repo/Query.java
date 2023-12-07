package com.github.kettoleon.hive4j.agents.repo;

//TODO decide if this goes into the API or not

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

import static com.github.kettoleon.hive4j.util.MarkdownUtils.markdownToHtml;

@Data
@Entity
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hive4jSwarmAgent agent;

    @Column(columnDefinition = "LONGTEXT")
    private String query;

    @Column(columnDefinition = "LONGTEXT")
    private String result;

    public String getResultAsHtml() {
        return markdownToHtml(result);
    }

    private ZonedDateTime created;

    private String createdBy;

}
