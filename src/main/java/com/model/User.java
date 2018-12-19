package com.model;

import com.helpers.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String login;

    @Column(unique = true, nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @ElementCollection
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")})
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "login: " + login;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addRole(Role role) {
        roles.add(role);
    }
}
