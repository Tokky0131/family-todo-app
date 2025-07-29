package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 誰でもアクセスできるパス（GET・POST 両方）
                .requestMatchers(HttpMethod.GET, "/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()

                .requestMatchers(HttpMethod.GET, "/users/register/confirm").permitAll()  // ←★追加
                .requestMatchers(HttpMethod.POST, "/users/register/confirm").permitAll() // ←★追加

                .requestMatchers(HttpMethod.GET, "/users/register/confirm").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register/confirm").permitAll()

                .requestMatchers(HttpMethod.GET, "/users/register-user/complete").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register-user/complete").permitAll()

                // 静的リソース
                .requestMatchers("/css/**", "/images/**").permitAll()

                // それ以外はログインが必要
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
