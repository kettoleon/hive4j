package com.github.kettoleon.hive4j.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Slf4j
@Configuration
public class FirstUserConfigurer {

    public static final String DEFAULT_ADMIN_EMAIL = "admin@localhost";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createFirstUser(Environment environment) {
        return args -> {
            if (userRepository.count() == 0) {
                Hive4jUser user = new Hive4jUser();
                user.setEmail(environment.getProperty("hive4j.admin.email", DEFAULT_ADMIN_EMAIL));
                String password = environment.getProperty("hive4j.admin.password", generateRandomPassword());
                user.setPasswordHash(passwordEncoder.encode(password));
                user.setName(environment.getProperty("hive4j.admin.name", "Administrator"));
                user.setRoles(List.of(new UserRole(user, "ADMIN")));
                userRepository.save(user);

                log.info("Created first admin user with email: {} and password: {}", user.getEmail(), password);

            }
        };
    }

    private static String passAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String passNum = "0123456789";
    private static String passSpecial = "!@#$&=.,;:-_+*";

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sb.append(randomChar(passAlpha));
        }
        for (int i = 0; i < 2; i++) {
            sb.append(randomChar(passNum));
        }
        for (int i = 0; i < 1; i++) {
            sb.append(randomChar(passSpecial));
        }
        scramble(sb);
        return sb.toString();
    }

    private void scramble(StringBuilder sb) {
        for (int i = 0; i < sb.length(); i++) {
            int randomIndex = (int) (Math.random() * sb.length());
            char temp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(randomIndex));
            sb.setCharAt(randomIndex, temp);
        }
    }

    private char randomChar(String passAlpha) {
        return passAlpha.charAt((int) (Math.random() * passAlpha.length()));
    }


}
