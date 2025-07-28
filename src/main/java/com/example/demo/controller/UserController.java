package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.CustomUserDetailsService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // アカウント登録画面表示
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register-user";  // register-user.html に遷移
    }

    // アカウント登録処理
    @PostMapping("/register")
    public String registerUser(
        @RequestParam String username,
        @RequestParam String password,
        Model model
    ) {
        if (customUserDetailsService.existsByUsername(username)) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            return "register-user";
        }

        customUserDetailsService.registerUser(username, password);
        return "redirect:/login";
    }
}
