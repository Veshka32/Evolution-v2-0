package com.config;

import com.model.User;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class PrincipalExcec implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        //String principalId = (String) map.get("id");

        String facebookId = (String) map.get("id");
        String name = (String) map.get("name");

        User user=new User();
        user.setLogin(name);

        return new User();
    }
}
