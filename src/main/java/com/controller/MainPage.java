package com.controller;

import com.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainPage {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("list",userRepository.findAll());
        return "index";
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "error", required = false) String error,Model model){
        if (error!=null){
            model.addAttribute("error","Wrong login or password");
        }
        return "login";
    }

    @GetMapping("/all")
    @ResponseBody
    public Iterable findAll() {
        return userRepository.findAll();
    }
}
