package com.github.kettoleon.hive4j.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Data
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = LAZY)
    private Hive4jUser user;

    private String role;

    public UserRole(Hive4jUser user, String role) {
        this.user = user;
        this.role = role;
    }
}
