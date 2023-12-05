package com.github.kettoleon.hive4j.model;

import java.util.List;

public interface InstructionSerializer {

    String serialize(List<Instruction> instruction);

}
