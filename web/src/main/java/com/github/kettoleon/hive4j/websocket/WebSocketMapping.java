package com.github.kettoleon.hive4j.websocket;

import com.github.kettoleon.hive4j.agents.QueriesWebSocketHandler;
import com.github.kettoleon.hive4j.models.ModelPullHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketMapping implements WebSocketConfigurer {

    @Autowired
    private ModelPullHandler modelPullsWebsocketHandler;

    @Autowired
    private QueriesWebSocketHandler queriesWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(modelPullsWebsocketHandler, "/api/v1/pulls/*");
        registry.addHandler(queriesWebsocketHandler, "/api/v1/queries/*");
    }
}
