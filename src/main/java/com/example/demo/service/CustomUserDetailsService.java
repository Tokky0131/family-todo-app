package com.example.demo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    // ✅ ユーザー名の重複チェック
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        return userRepository.findByUsername(username.trim()).isPresent();
    }

    // ✅ ユーザー登録（バリデーション込み）
    public boolean registerUser(String username, String rawPassword) {
        try {
            // --- 1. null / 空文字 / 空白のみチェック ---
            if (username == null || rawPassword == null) return false;
            username = username.trim();
            rawPassword = rawPassword.trim();
            if (username.isEmpty() || rawPassword.isEmpty()) return false;

            // --- 2. 重複チェック ---
            if (userRepository.findByUsername(username).isPresent()) {
                return false;
            }

            // --- 3. パスワード最低限チェック ---
            if (rawPassword.length() < 8) {
                return false;
            }

            // --- 4. パスワードハッシュ化 ---
            String encodedPassword = passwordEncoder.encode(rawPassword);

            // --- 5. 保存 ---
            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword);

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // ログ出力
            return false;
        }
    }
}
