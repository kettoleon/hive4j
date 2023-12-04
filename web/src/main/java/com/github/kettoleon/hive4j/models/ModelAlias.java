package com.github.kettoleon.hive4j.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ModelAlias {

    @Id
    private String id;

    private String backendId;
    private String backendModelId;

    private String alias;

}
