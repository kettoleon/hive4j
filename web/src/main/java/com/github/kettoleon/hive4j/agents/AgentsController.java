package com.github.kettoleon.hive4j.agents;

import com.github.kettoleon.hive4j.agent.SwarmAgent;
import com.github.kettoleon.hive4j.agents.repo.AgentsRepository;
import com.github.kettoleon.hive4j.agents.repo.Hive4jSwarmAgent;
import com.github.kettoleon.hive4j.agents.repo.Query;
import com.github.kettoleon.hive4j.agents.repo.QueryRepository;
import com.github.kettoleon.hive4j.queue.Message;
import com.github.kettoleon.hive4j.queue.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;
import static java.time.ZonedDateTime.now;

@Controller
@Slf4j
public class AgentsController {

    @Autowired
    private AgentsRepository agentsRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private Publisher<Query, Void> queryPublisher;

    @GetMapping(path = {"/agents"})
    public ModelAndView agents() {
        return page("agents/agents", "Agents")
                .addObject("agents", agentsRepository.findAll());
    }

    @GetMapping(path = {"/agents/{agentId}"})
    public ModelAndView inspectAgent(@PathVariable("agentId") String agentId) {
        Hive4jSwarmAgent agent = agentsRepository.findById(agentId).orElseThrow();
        return page("agents/inspect/agent-inspect", agent.getName())
                .addObject("agent", agent)
                .addObject("queries", queryRepository.findByAgentOrderByCreatedDesc(agent))
                ;
    }


    @GetMapping(path = {"/api/v1/agents"})
    public ModelAndView searchAgents(@RequestParam(value = "search", required = false) String search) {
        if (StringUtils.isBlank(search)) {
            return new ModelAndView("agents/agent-table")
                    .addObject("agents", agentsRepository.findAll());
        }
        //TODO search agents when we start to have a lot of them
        return new ModelAndView("agents/agent-table")
                .addObject("agents", agentsRepository.findAll());
    }

    @GetMapping(path = {"/api/v1/agents/new"})
    public ModelAndView newAgent() {
        return new ModelAndView("agents/agent-modal-new");
    }

    @GetMapping(path = {"/api/v1/agents/{agentId}/edit"})
    public ModelAndView editAgent(@PathVariable("agentId") String agentId) {
        return new ModelAndView("agents/agent-modal-edit")
                .addObject("agent", agentsRepository.findById(agentId).orElseThrow());
    }

    @GetMapping(path = {"/api/v1/agents/{agentId}/delete"})
    public ModelAndView deleteAgentConfirm(@PathVariable("agentId") String agentId) {
        return new ModelAndView("agents/agent-modal-delete")
                .addObject("agent", agentsRepository.findById(agentId).orElseThrow());
    }

    @DeleteMapping(path = {"/api/v1/agents"})
    @ResponseBody
    public String deleteAgent(@RequestParam("agentId") String agentId) {
        agentsRepository.deleteById(agentId);
        return "";
    }

    @PostMapping(path = {"/api/v1/agents"})
    @ResponseBody
    public ModelAndView postAgent(@ModelAttribute("formAgent") SwarmAgent swarmAgent) {
        log.info("postAgent {} ", swarmAgent);
        Hive4jSwarmAgent newOrExistingAgent = agentsRepository.findById(swarmAgent.getId())
                .orElse(new Hive4jSwarmAgent());

        agentsRepository.save(mapToHive4jAgent(swarmAgent, newOrExistingAgent));
        return new ModelAndView("agents/agents-table-row").addObject("agent", newOrExistingAgent);
    }

    @PostMapping(path = {"/api/v1/agents/{agentId}/query"})
    public ModelAndView queryAgent(@PathVariable("agentId") String agentId, @RequestParam("query") String query) {
        Hive4jSwarmAgent agent = agentsRepository.findById(agentId).orElseThrow();
        Query queryEntity = queryRepository.save(createNewQuery(agent, query));
        queryPublisher.fireAndForget(new Message<>(queryEntity.getId(), UUID.randomUUID().toString(), queryEntity));
        return new ModelAndView("agents/inspect/agent-query-card")
                .addObject("query", queryEntity);
    }

    @DeleteMapping(path = {"/api/v1/query/{queryId}"})
    @ResponseBody
    public String queryAgent(@PathVariable("queryId") String queryId) {
        queryRepository.deleteById(queryId);
        return "";
    }

    private static Query createNewQuery(Hive4jSwarmAgent agent, String query) {
        Query qe = new Query();
        qe.setQuery(query);
        qe.setAgent(agent);
        qe.setCreated(now());
        qe.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        return qe;
    }

    private Hive4jSwarmAgent mapToHive4jAgent(SwarmAgent swarmAgent, Hive4jSwarmAgent agent) {
        agent.setId(swarmAgent.getId());
        agent.setName(swarmAgent.getName());
        agent.setStatements(mapStatements(swarmAgent));
        agent.setTraits(swarmAgent.getTraits());
        agent.setSpecialty(swarmAgent.getSpecialty());
        agent.setMission(swarmAgent.getMission());
        agent.setFunction(swarmAgent.getFunction());
        return agent;
    }

    private static List<String> mapStatements(SwarmAgent swarmAgent) {
        return Arrays.stream(swarmAgent.getStatements().get(0).split("\n")).map(String::trim).collect(Collectors.toList());
    }


}
