package com.github.kettoleon.hive4j.agents;

import com.github.kettoleon.hive4j.agents.repo.Query;
import com.github.kettoleon.hive4j.agents.repo.QueryRepository;
import com.github.kettoleon.hive4j.model.GenerateProgress;
import com.github.kettoleon.hive4j.util.MarkdownUtils;
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
    private Map<String, GenerateProgress> responses = new HashMap<>();
    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    public void addQuery(Query query, Flux<GenerateProgress> response) {
        liveQueries.put(query.getId(), query);
        response.doFinally(s -> closeAllSessions(query))
                .subscribe(gp -> {
                    responses.put(query.getId(), gp);
                    broadcastRawMessage(query.getId(), buildProgressHtml(query, gp));
                });
    }

    private String getResponse(String queryId) {
        return responses.get(queryId).getFullResponse();
    }

    private void closeAllSessions(Query query) {
        GenerateProgress gp = responses.get(query.getId());
        if(!gp.isError()) {
            query.setResult(getResponse(query.getId()));
            queriesRepository.save(query);
        }

        responses.remove(query.getId());
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

    private String buildProgressHtml(Query query, GenerateProgress gp) {
        if(gp.isError()){
            return String.format("<div id=\"qr-%s\" class=\"alert alert-danger d-flex align-items-center\" role=\"alert\"><i role=\"img\" class=\"bi bi-exclamation-circle-fill flex-shrink-0 me-2\"></i><div>%s</div></div>", query.getId(), gp.getErrorMessage());
        }
        if (StringUtils.isBlank(gp.getFullResponse())) {
            return String.format("<div id=\"qr-%s\" class=\"spinner-border spinner-border-sm\" role=\"status\"><span class=\"visually-hidden\">Loading...</span></div>", query.getId());
        }
        if (gp.isDone()) {
            return String.format("<span id=\"qr-%s\">%s</span><li id=\"query-status-%s\" class=\"list-group-item\">Generated %d tokens in %.2f seconds. (%s tokens/second)</li>",
                    query.getId(),
                    MarkdownUtils.markdownToHtml(gp.getFullResponse()),
                    query.getId(),
                    gp.getResponseTokens(),
                    gp.getResponseSeconds(),
                    gp.getHumanReadableTokensPerSecond()

            );
        }
        return String.format("<span id=\"qr-%s\">%s</span>", query.getId(), MarkdownUtils.markdownToHtml(gp.getFullResponse()));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String queryId = getQueryId(session);
        getOrCreateWebSockets(queryId).add(session);

        if (responses.containsKey(queryId)) {
            sendRawMessage(session, buildProgressHtml(liveQueries.get(queryId), responses.get(queryId)));
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
