package com.github.kettoleon.hive4j.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class Hive4jUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .map(this::toSpringUser)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

    }

    private User toSpringUser(Hive4jUser user) {
        return new User(user.getEmail(), user.getPasswordHash(), true, true,
                true, true, getAuthorities(user));
    }

    private static List<GrantedAuthority> getAuthorities(Hive4jUser user) {
        return user.getRoles().stream()
                .map(UserRole::getRole)
                .map(r -> "ROLE_" + r.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
