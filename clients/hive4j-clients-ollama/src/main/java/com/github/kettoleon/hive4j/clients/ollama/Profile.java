package com.github.kettoleon.hive4j.clients.ollama;


import com.github.kettoleon.hive4j.clients.ollama.model.Options;

import java.util.function.Consumer;

public enum Profile {
    ;

    private final Consumer<Options.OptionsBuilder> requestConfigurator;

    Profile(Consumer<Options.OptionsBuilder> requestConfigurator) {

        this.requestConfigurator = requestConfigurator;
    }

    public void apply(Options.OptionsBuilder request) {
        requestConfigurator.accept(request);
    }

}
