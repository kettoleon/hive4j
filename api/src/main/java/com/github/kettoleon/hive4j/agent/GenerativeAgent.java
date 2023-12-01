package com.github.kettoleon.hive4j.agent;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * At this level, generative agents can still be intended for human behaviour simulation or mission-oriented purposes.
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class GenerativeAgent extends Agent {

    private List<String> statements;
    private List<String> traits;

}
