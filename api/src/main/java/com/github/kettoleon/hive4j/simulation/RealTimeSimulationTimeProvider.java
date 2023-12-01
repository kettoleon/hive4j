package com.github.kettoleon.hive4j.simulation;

import java.time.ZonedDateTime;

public class RealTimeSimulationTimeProvider implements SimulationTimeProvider {
    @Override
    public ZonedDateTime getCurrentTime() {
        return ZonedDateTime.now();
    }
}
