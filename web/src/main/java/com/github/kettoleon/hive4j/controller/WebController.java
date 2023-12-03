package com.github.kettoleon.hive4j.controller;

import com.github.kettoleon.hive4j.backend.Backend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;

@Controller
@Slf4j
public class WebController {

    @Autowired
    private List<Backend> backends;

    @GetMapping(path = {"", "/", "/home"})
    public ModelAndView dashboard() {
        return page("home", "Home");
    }

    @GetMapping(path = {"/agents"})
    public ModelAndView agents() {
        return page("agents/agents", "Agents");
    }

    @GetMapping(path = {"/backends"})
    public ModelAndView backends() {
        return page("backends/backends", "Backends").addObject("backends", backends);
    }

    @GetMapping(path = {"/workspaces"})
    public ModelAndView workspaces() {
        return page("workspaces/workspaces", "Workspaces");
    }

}
