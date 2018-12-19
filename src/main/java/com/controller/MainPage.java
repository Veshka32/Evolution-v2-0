package com.controller;

import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPage {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String hello() {
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
