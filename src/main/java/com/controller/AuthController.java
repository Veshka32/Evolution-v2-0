package com.controller;

import com.model.UserDTO;
import com.services.UserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;


@Controller
public class AuthController {

    @Autowired
    UserServiceI userService;

    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Wrong login or password");
        }
        return "login";
    }

    @GetMapping(value = "/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping(value = "/register")
    public String addUser(@ModelAttribute("user") @Valid UserDTO dto, BindingResult result, Model model, RedirectAttributes attr) {
        if (result.hasErrors()) {
            model.addAttribute("user", dto);
            return "register";
        }

        Optional<String> error = userService.createUser(dto);
        if (error.isPresent()) {
            model.addAttribute("error", error.get());
            model.addAttribute("user", dto);
            return "register";
        }

        return "test";
    }

}
