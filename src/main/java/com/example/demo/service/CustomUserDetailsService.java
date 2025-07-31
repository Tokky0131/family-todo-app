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
    private final PasswordEncoder passwordEncoder; // 🔧 追加：DIで注入

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // 🔧 追加：コンストラクタで受け取る
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
        return userRepository.findByUsername(username).isPresent();
    }

    // ✅ ユーザー登録（パスワード暗号化付き）
    public boolean registerUser(String username, String rawPassword) {
        try {
            String encodedPassword = passwordEncoder.encode(rawPassword); // 🔧 修正：DIで使う

            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword);

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 任意でログ出力
            return false;
        }
    }
}
