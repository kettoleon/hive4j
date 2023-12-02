package com.github.kettoleon.hive4j.websocket;

import com.github.kettoleon.hive4j.clients.ollama.OLlamaClient;
import com.github.kettoleon.hive4j.clients.ollama.model.DownloadStatus;
import com.github.kettoleon.hive4j.model.Model;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class ModelPullHandler implements WebSocketHandler {

    @Autowired
    private OLlamaClient oLlamaClient;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private Map<String, Model> pullingModels = new HashMap<>();
    private Map<String, DownloadStatus> lastStatuses = new HashMap<>();
    private final Map<String, List<WebSocketSession>> pullingSessions = new ConcurrentHashMap<>();

    public void pullModel(Model model) {
        pullingModels.put(model.getPullProgressId(), model);
        oLlamaClient.pullModel(model.getRepositoryId())
                .subscribe(ds -> {
                    lastStatuses.put(model.getPullProgressId(), ds);
                    broadcastRawMessage(model.getPullProgressId(), buildProgressHtml(model, ds));
                    if (ds.isSuccess()) {
                        log.info("Pulling finished for model: {}", model.getRepositoryId());
                        closeAllSessions(model.getPullProgressId());
                    }
                    if (ds.isError()) {
                        log.error("Error pulling model: {}, {}", model.getRepositoryId(), ds.getError());
                        new Thread(() -> {
                            try {
                                Thread.sleep(10000L);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            closeAllSessions(model.getPullProgressId());
                        }).start();

                    }
                });
    }

    private void closeAllSessions(String pullProgressId) {
        lastStatuses.remove(pullProgressId);
        pullingModels.remove(pullProgressId);
        pullingSessions.get(pullProgressId).forEach(session -> {
            try {
                session.close();
            } catch (IOException e) {
                log.warn("Error closing session: {}", session, e);
            }
        });
        pullingSessions.remove(pullProgressId);
    }

    private void broadcastRawMessage(String pullProgressId, String html) {
        getOrCreateWebSockets(pullProgressId).forEach(session -> sendRawMessage(session, html));
    }

    private List<WebSocketSession> getOrCreateWebSockets(String pullProgressId) {
        return pullingSessions.computeIfAbsent(pullProgressId, k -> new CopyOnWriteArrayList<>());
    }

    private void sendRawMessage(WebSocketSession session, String progressHtml) {
        try {
            session.sendMessage(new TextMessage(progressHtml));
        } catch (IOException e) {
            log.warn("Error sending message to session: {}", session, e);
        }
    }

    private String buildProgressHtml(Model model, DownloadStatus ds) {
        Context context = new Context();
        context.setVariable("status", ds);
        context.setVariable("pullProgressId", model.getPullProgressId());
        return templateEngine.process("models/model-pull-progress", context);
    }

    public List<Model> getPullingModels() {
        return new ArrayList<>(pullingModels.values());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String pullProgressId = getPullProgressId(session);
        getOrCreateWebSockets(pullProgressId).add(session);

        if (lastStatuses.containsKey(pullProgressId)) {
            sendRawMessage(session, buildProgressHtml(pullingModels.get(pullProgressId), lastStatuses.get(pullProgressId)));
        }
    }

    private String getPullProgressId(WebSocketSession session) {
        return StringUtils.substringAfterLast(session.getUri().toString(), "/");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        getOrCreateWebSockets(getPullProgressId(session)).remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        getOrCreateWebSockets(getPullProgressId(session)).remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
