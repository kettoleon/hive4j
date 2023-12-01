package com.github.kettoleon.hive4j.simulation;

import java.time.ZonedDateTime;

public interface SimulationTimeProvider {

    ZonedDateTime getCurrentTime();

}
