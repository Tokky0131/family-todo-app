package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.form.UserForm;
import com.example.demo.service.CustomUserDetailsService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // --- 即登録用の既存フロー（残す） ---
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register-user";
    }

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
        return "redirect:/login-page?registered"; // ✅ 登録完了メッセージを1回だけ表示
    }

    // --- 確認画面付きの新フロー（改訂版） ---

    @PostMapping("/register/confirm")
    public String confirmRegistration(@ModelAttribute UserForm userForm, Model model) {
        if (customUserDetailsService.existsByUsername(userForm.getUsername())) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            return "register-user";
        }

        model.addAttribute("userForm", userForm);
        return "register-user-confirm";
    }

    @PostMapping("/register-user/complete")
    public String completeRegistration(@ModelAttribute UserForm userForm, Model model) {
        boolean success = customUserDetailsService.registerUser(
            userForm.getUsername(),
            userForm.getPassword()
        );

        if (!success) {
            model.addAttribute("error", "登録に失敗しました。もう一度お試しください。");
            model.addAttribute("userForm", userForm);
            return "register-user-confirm";
        }

        return "redirect:/login?registered";  // ✅ 成功時、登録完了メッセージ付きでログイン画面へ
    }
}
