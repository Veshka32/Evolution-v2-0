package com.services.implementations;

import org.springframework.beans.factory.annotation.Value;

public class FacebookService {

    @Value("${spring.social.facebook.appId}")
    String facebookAppId;
    @Value("${spring.social.facebook.appSecret}")
    String facebookSecret;

}
