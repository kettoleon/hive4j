package com.github.kettoleon.hive4j.agents.repo;

//TODO decide if this goes into the API or not

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Hive4jSwarmAgent agent;

    private String query;

    private String result;

    private ZonedDateTime created;

    private String createdBy;

}
