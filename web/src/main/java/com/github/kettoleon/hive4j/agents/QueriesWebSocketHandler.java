package com.github.kettoleon.hive4j.agents;

import com.github.kettoleon.hive4j.agents.repo.Query;
import com.github.kettoleon.hive4j.agents.repo.QueryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class QueriesWebSocketHandler implements WebSocketHandler {

    @Autowired
    private QueryRepository queriesRepository;

    private Map<String, Query> liveQueries = new HashMap<>();
    private Map<String, StringBuilder> accumulatedResponses = new HashMap<>();
    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void addQuery(Query query, Flux<String> response) {
        liveQueries.put(query.getId(), query);
        accumulatedResponses.put(query.getId(), new StringBuilder());
        response.doFinally(s -> closeAllSessions(query))
                .subscribe(ds -> {
                    StringBuilder sb = accumulatedResponses.get(query.getId());
                    sb.append(ds);
                    broadcastRawMessage(query.getId(), buildProgressHtml(query, sb.toString()));
                });
    }

    private void closeAllSessions(Query query) {
        query.setResult(accumulatedResponses.get(query.getId()).toString());
        queriesRepository.save(query);

        accumulatedResponses.remove(query.getId());
        liveQueries.remove(query.getId());
        sessions.get(query.getId()).forEach(session -> {
            try {
                session.close();
            } catch (IOException e) {
                log.warn("Error closing session: {}", session, e);
            }
        });
        sessions.remove(query.getId());
    }

    private void broadcastRawMessage(String queryId, String html) {
        getOrCreateWebSockets(queryId).forEach(session -> sendRawMessage(session, html));
    }

    private List<WebSocketSession> getOrCreateWebSockets(String queryId) {
        return sessions.computeIfAbsent(queryId, k -> new CopyOnWriteArrayList<>());
    }

    private void sendRawMessage(WebSocketSession session, String progressHtml) {
        try {
            session.sendMessage(new TextMessage(progressHtml));
        } catch (IOException e) {
            log.warn("Error sending message to session: {}", session, e);
        }
    }

    private String buildProgressHtml(Query query, String response) {
        if (StringUtils.isBlank(response)) {
            return String.format("<div id=\"qr-%s\" class=\"spinner-border spinner-border-sm\" role=\"status\"><span class=\"visually-hidden\">Loading...</span></div>", query.getId());
        }
        return String.format("<span id=\"qr-%s\">%s</span>", query.getId(), response).replaceAll("\n", "<br/>");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String queryId = getQueryId(session);
        getOrCreateWebSockets(queryId).add(session);

        if (accumulatedResponses.containsKey(queryId)) {
            sendRawMessage(session, buildProgressHtml(liveQueries.get(queryId), accumulatedResponses.get(queryId).toString()));
        }
    }

    private String getQueryId(WebSocketSession session) {
        return StringUtils.substringAfterLast(session.getUri().toString(), "/");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        getOrCreateWebSockets(getQueryId(session)).remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        getOrCreateWebSockets(getQueryId(session)).remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
