package com.github.kettoleon.hive4j.agents;

import com.github.kettoleon.hive4j.agent.SwarmAgent;
import com.github.kettoleon.hive4j.agents.repo.AgentsRepository;
import com.github.kettoleon.hive4j.agents.repo.Hive4jSwarmAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;

@Controller
@Slf4j
public class AgentsController {

    @Autowired
    private AgentsRepository agentsRepository;

    @GetMapping(path = {"/agents"})
    public ModelAndView agents() {
        return page("agents/agents", "Agents")
                .addObject("agents", agentsRepository.findAll());
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
        agentsRepository.findById(agentId).ifPresent(agentsRepository::delete);
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
