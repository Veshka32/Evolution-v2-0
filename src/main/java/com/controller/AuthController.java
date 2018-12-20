package com.controller;

import com.model.UserDTO;
import com.services.UserServiceI;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;


@Controller
public class AuthController {
    private static final String REGISTER_PAGE = "register";
    private static final String USER = "user";
    private static final String ERROR = "error";

    @Autowired
    UserServiceI userService;

    @GetMapping(value = "/login")
    public String login(@RequestParam(value = ERROR, required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute(ERROR, "Wrong login or password");
        }
        return "login";
    }

    @GetMapping(value = "/register")
    public String showRegister(Model model) {
        model.addAttribute(USER, new UserDTO());
        return REGISTER_PAGE;
    }

    @PostMapping(value = "/register")
    public String addUser(@ModelAttribute(USER) @Valid UserDTO dto, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute(USER, dto);
            return REGISTER_PAGE;
        }

        Optional<String> error = userService.createUser(dto);
        if (error.isPresent()) {
            model.addAttribute(ERROR, error.get());
            model.addAttribute(USER, dto);
            return REGISTER_PAGE;
        }
        try {
            request.login(dto.getLogin(), dto.getPassword());
        } catch (ServletException e) {
            LoggerFactory.getLogger(AuthController.class).warn("failed log in after sign up: ", e);
            return "login";
        }
        return "redirect:/";
    }

}
