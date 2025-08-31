package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.form.UserForm;
import com.example.demo.service.CustomUserDetailsService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    // --- 入力画面表示 ---
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserForm());
        }
        return "register-user";
    }

    // --- 即登録フロー（既存維持） ---
    // POST /users/register に対して、@Valid でサーバ側検証を有効化
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("userForm") @Valid UserForm userForm,
            BindingResult bindingResult,
            Model model
    ) {
        // 1) 入力検証（null/空文字/空白のみ/長さ）
        if (bindingResult.hasErrors()) {
            return "register-user";
        }

        // 2) 重複ユーザー名
        if (customUserDetailsService.existsByUsername(userForm.getUsername())) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            return "register-user";
        }

        // 3) 登録
        boolean success = customUserDetailsService.registerUser(
                userForm.getUsername(),
                userForm.getPassword()
        );
        if (!success) {
            model.addAttribute("error", "登録に失敗しました。もう一度お試しください。");
            return "register-user";
        }

        // 4) 成功時：ログイン画面へ（1回だけのメッセージはクエリ ?registered で表示）
        return "redirect:/login?registered";
    }

    // --- 確認画面付きの新フロー（改訂版） ---
    @PostMapping("/register/confirm")
    public String confirmRegistration(
            @ModelAttribute("userForm") @Valid UserForm userForm,
            BindingResult bindingResult,
            Model model
    ) {
        // 1) 入力検証
        if (bindingResult.hasErrors()) {
            return "register-user";
        }

        // 2) 重複ユーザー名（確認画面に進む前にもチェック）
        if (customUserDetailsService.existsByUsername(userForm.getUsername())) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            return "register-user";
        }

        // 3) 確認画面へ
        model.addAttribute("userForm", userForm);
        return "register-user-confirm";
    }

    @PostMapping("/register-user/complete")
    public String completeRegistration(
            @ModelAttribute("userForm") @Valid UserForm userForm,
            BindingResult bindingResult,
            Model model
    ) {
        // 1) 入力検証（隠しフィールド改ざん対策で再度実施）
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "入力値を再確認してください。");
            return "register-user";
        }

        // 2) 重複ユーザー名（念のためこちらでも再チェック）
        if (customUserDetailsService.existsByUsername(userForm.getUsername())) {
            model.addAttribute("error", "そのユーザー名はすでに使われています");
            model.addAttribute("userForm", userForm);
            return "register-user-confirm";
        }

        // 3) 登録処理
        boolean success = customUserDetailsService.registerUser(
                userForm.getUsername(),
                userForm.getPassword()
        );

        if (!success) {
            model.addAttribute("error", "登録に失敗しました。もう一度お試しください。");
            model.addAttribute("userForm", userForm);
            return "register-user-confirm";
        }

        // 4) 成功時：ログイン画面へ（?registered で完了メッセージ）
        return "redirect:/login?registered";
    }
}
