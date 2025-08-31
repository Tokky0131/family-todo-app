package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録フォーム
 * - ユーザー名: 必須、3〜50文字、空白禁止
 * - パスワード: 必須、8〜100文字
 */
public class UserForm {

    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 3, max = 50, message = "ユーザー名は3〜50文字で入力してください")
    @Pattern(regexp = "^[^\\s]+$", message = "ユーザー名に空白は使えません")
    private String username;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, max = 100, message = "パスワードは8〜100文字で入力してください")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = (username == null) ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = (password == null) ? null : password.trim();
    }
}
