package com.github.kettoleon.hive4j.models;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelAliasesRepository extends JpaRepositoryImplementation<ModelAlias, String> {

    List<ModelAlias> findAllByBackendIdAndBackendModelId(String backendId, String backendModelId);

    default List<String> getAliases(String backendId, String backendModelId){
        return findAllByBackendIdAndBackendModelId(backendId, backendModelId).stream().map(ModelAlias::getAlias).collect(java.util.stream.Collectors.toList());
    }

    default String getCommaSeparatedAliases(String backendId, String backendModelId){
        return String.join(", ", getAliases(backendId, backendModelId));
    }

    List<ModelAlias> findAllByAlias(String alias);

}
