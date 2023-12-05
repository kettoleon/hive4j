package com.github.kettoleon.hive4j.agents.repo;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepositoryImplementation<Query, String> {


}
