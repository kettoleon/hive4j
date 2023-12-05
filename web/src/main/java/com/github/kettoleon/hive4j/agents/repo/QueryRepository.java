package com.github.kettoleon.hive4j.agents.repo;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepositoryImplementation<Query, String> {

    List<Query> findByAgentOrderByCreatedDesc(Hive4jSwarmAgent agent);

}
