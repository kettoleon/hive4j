package com.github.kettoleon.hive4j.clients.ssh;

import com.jcraft.jsch.JSch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SshConfiguration {

    @Bean
    public JSch jsch(){
        return new JSch();
    }


}
