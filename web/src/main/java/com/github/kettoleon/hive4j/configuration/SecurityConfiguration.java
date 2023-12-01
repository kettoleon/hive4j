package com.github.kettoleon.hive4j.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Bean
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req ->
                        req
//                                .requestMatchers("/users").hasRole("ADMIN")
//                                .requestMatchers("/bet").authenticated()
                                .anyRequest().permitAll()
                )
//                .formLogin(login -> login
//                        .loginPage("/login").permitAll()
//                        .loginProcessingUrl("/login").permitAll()
//                        .defaultSuccessUrl("/bet")
//
//                )
//                .userDetailsService(new InMemoryUserDetailsManager())
//                .logout(logout -> logout.permitAll().logoutUrl("/logout").logoutSuccessUrl("/rules"))
        ;

        return http.build();

    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
