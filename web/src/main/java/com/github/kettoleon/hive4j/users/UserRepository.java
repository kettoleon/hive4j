package com.github.kettoleon.hive4j.users;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepositoryImplementation<Hive4jUser, String> {
    Optional<Hive4jUser> findByEmail(String email);

    Optional<Hive4jUser> findByName(String name);
}
