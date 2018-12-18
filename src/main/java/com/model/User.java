package com.model;

import com.services.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true,nullable = false)
    private String login;

    @Column(unique=true,nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @ElementCollection
    @JoinTable(name = "user_roles",
            joinColumns = { @JoinColumn(name = "user_id") })
    private Set<Role> roles=new HashSet<>();

    @Override
    public String toString(){
        return "login: "+login;
    }

    public Set<Role> getRoles(){
        return roles;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
