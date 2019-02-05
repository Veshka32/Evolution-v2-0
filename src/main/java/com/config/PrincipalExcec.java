package com.config;

import com.model.User;
import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class PrincipalExcec implements PrincipalExtractor {

    @Autowired
    UserRepository repository;

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //String principalId = (String) map.get("id");


        String name = (String) map.get("name");
        User user=repository.findByLogin(name);
        if (user==null){
            user=new User();
            user.setLogin(name);
            user.setEmail((String)map.get("email"));
        }

        return user;
    }
}
