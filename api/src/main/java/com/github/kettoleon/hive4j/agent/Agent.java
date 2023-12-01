package com.github.kettoleon.hive4j.agent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@ToString
@SuperBuilder
public abstract class Agent {

    /**
     * An agent's id might be used for e-mail/messaging accounts.
     * For example: jane.sheppard
     */
    private String id;

    /**
     * Give them just a name or name and surname, depending on the complexity of what you are building.
     */
    private String name;


}
