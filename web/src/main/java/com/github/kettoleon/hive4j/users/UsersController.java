package com.github.kettoleon.hive4j.users;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.configuration.GlobalTemplateVariables.page;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Controller
@Slf4j
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(path = {"/users"})
    public ModelAndView login() {
        return page("users/users", "User Administration")
                .addObject("users", userRepository.findAll());
    }

    @GetMapping(path = {"/api/v1/users/new"})
    public ModelAndView newUser() {
        return new ModelAndView("users/user-modal");
    }

    @GetMapping(path = {"/api/v1/users/{email}/edit"})
    public ModelAndView editUser(@PathVariable("email") String email) {
        return new ModelAndView("users/user-modal")
                .addObject("h4juser", userRepository.findByEmail(email).orElseThrow());
    }

    @DeleteMapping(path = {"/api/v1/users/{email}"})
    @ResponseBody
    public void deleteUser(@PathVariable("email") String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }

    @PostMapping(path = {"/api/v1/users"})
    @ResponseBody
    public void postUser(@RequestParam("email") String email, @RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("roles") String roles) {
        List<String> parsedRoles = parseRoles(roles);
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    user.setName(userName);
                    if (isNotBlank(password)) {
                        user.setPasswordHash(passwordEncoder.encode(password));
                    }
                    modifyRoles(user, parsedRoles);
                    userRepository.save(user);
                },
                () -> userRepository.save(newUser(email, userName, password, parsedRoles))
        );
    }

    private Hive4jUser newUser(String email, String userName, String password, List<String> roles) {
        Hive4jUser user = new Hive4jUser();
        user.setEmail(email);
        user.setName(userName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(roles.stream()
                .map(role -> new UserRole(user, role))
                .collect(Collectors.toList()));
        return user;
    }

    private static List<String> parseRoles(String roles) {
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    private void modifyRoles(Hive4jUser user, List<String> roles) {
        List<UserRole> toRemove = new ArrayList<>();
        for (UserRole role : user.getRoles()) {
            if (!roles.contains(role.getRole())) {
                toRemove.add(role);
            }
        }
        user.getRoles().removeAll(toRemove);
        for (String role : roles) {
            if (user.getRoles().stream().noneMatch(userRole -> userRole.getRole().equals(role))) {
                user.getRoles().add(new UserRole(user, role));
            }
        }
    }

}
