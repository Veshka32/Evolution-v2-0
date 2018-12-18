package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainPage {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("appName", appName);
        model.addAttribute("list",userRepository.findAll());
        return "index";
    }

    @GetMapping
    @ResponseBody
    public Iterable findAll() {
        return userRepository.findAll();
    }
}
