package com.github.kettoleon.hive4j.functions.experimental;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.kettoleon.hive4j.agent.SwarmAgent;
import com.github.kettoleon.hive4j.model.GenerateProgress;
import com.github.kettoleon.hive4j.model.Instruction;
import com.github.kettoleon.hive4j.model.Model;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
@Slf4j
public class ResearchFunction {

    private final WebClient webClient;
    @Autowired(required = false)
    private Model logicModel;


    public ResearchFunction() {
        webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    public void research(String prompt, SwarmAgent agent) {
        String searchQueries = logicModel.generate(Instruction.builder()
                .system("""
                        You are part of an AI system that evaluates prompts to figure out the best search engine queries.
                        The prompt will ask the AI system to research a topic, think of the best search engine queries to look for information about the topic.
                        You will not answer any prompts, you will only answer with the best search engine queries you can come up with.
                        Provide the answer in the following json format: {"queries": ["query1", "query2"], "notes": "reasoning, doubts, clarifications or explanations"}
                        """)
                .prompt(prompt)
                .build()).blockLast().getFullResponse();

        try {
            log.info("Researching topic on the internet from:\n{}\n\nto:\n{}", prompt, searchQueries);
            ResearchQueriesResponse resp = new ObjectMapper().readValue(searchQueries, ResearchQueriesResponse.class);
            for (String query : resp.queries) {
                List<SearchResult> results = duckDuckGoSearch(query);

                results.forEach(r -> {
                    System.out.println(r.title + "\n (" + r.url + ")\n\n" + r.description + "\n\n");
                    String text = navigateToDoc(fromHttpUrl(r.url).build().toUri()).asNormalizedText();
                    System.out.println("Summary:\n\n");
                    Flux<GenerateProgress> summary = summarize(text);
                    summary
                            .doFinally(st -> System.out.println(" === END SUMMARY ==="))
                            .doOnEach(gp -> {
                                if (gp.get().isError()) {
                                    System.err.println(gp.get().getErrorMessage());
                                }
                            })
                            .doOnEach(gp -> System.out.print(gp.get().getPartResponse()))
                            .doOnError(t -> System.err.println(t))
                            .blockLast();

                });

            }
        } catch (JsonProcessingException e) {
            log.warn("Error parsing response into json: {}", searchQueries, e);
        }
    }

    private Flux<GenerateProgress> summarize(String text) {
        return logicModel.generate(Instruction.builder()
                .system("""
                        You are part of an AI system that summarizes articles found on the internet to their smallest form.
                        The prompt will contain the text of an html page and you need to summarize the best you can.
                        You will not answer any prompts, you will only answer with the summary of the prompt you received.
                        """)
                .prompt(text)
                .build());
    }

    private List<SearchResult> duckDuckGoSearch(String query) {
        log.info("Performing duckduckgo search: {}", query);

        ArrayList<ResearchFunction.SearchResult> searchResults = new ArrayList<>();
        try {
            HtmlPage page = webClient.getPage("https://html.duckduckgo.com/html/?q=" + query);

            page.querySelectorAll("div.result__body").forEach(element -> {
                String title = element.querySelectorAll("a.result__a").getFirst().asNormalizedText();
                String url = "https://" + element.querySelectorAll("a.result__url").getFirst().asNormalizedText();
                String desc = element.querySelectorAll("a.result__snippet").getFirst().asNormalizedText();
                ResearchFunction.SearchResult sr = new ResearchFunction.SearchResult();
                sr.setDescription(desc);
                sr.setUrl(url);
                sr.setTitle(title);
                searchResults.add(sr);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Performing duckduckgo search: {} ({} results)", query, searchResults.size());
        return searchResults;
    }

    private HtmlPage navigateToDoc(URI uri) {
        log.info("Visiting {}", uri.toString());
        try {
            return webClient.getPage(uri.toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Data
    @NoArgsConstructor
    private static class ResearchQueriesResponse {
        private List<String> queries;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    private static class SearchResultResponse {
        private List<SearchResult> results;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    public static class SearchResult {
        private String title;
        private String url;
        private String description;
    }


}
