package com.github.kettoleon.hive4j.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.kettoleon.hive4j.model.Model.toSafeCssIdentifier;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;


@Entity
@Data
@NoArgsConstructor
public class Hive4jUser {

    @Id
    private String email;

    private String name;
    private String passwordHash;

    @OneToMany(mappedBy = "user", fetch = EAGER, cascade = ALL, orphanRemoval = true)
    private List<UserRole> roles;

    public String getCommaSeparatedRoles() {
        return roles.stream()
                .map(UserRole::getRole)
                .collect(Collectors.joining(", "));
    }

    public String getHtmlId() {
        return toSafeCssIdentifier(email);
    }

}
