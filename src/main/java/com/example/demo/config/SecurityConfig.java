package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 誰でもアクセスできるパス（GET・POST 両方）
                .requestMatchers(HttpMethod.GET, "/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/register/confirm").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register/confirm").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/register-user/complete").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register-user/complete").permitAll()

                // ✅ 追加：ログイン画面（GETだけ）
                .requestMatchers(HttpMethod.GET, "/login-page").permitAll()

                // 静的リソース
                .requestMatchers("/css/**", "/images/**").permitAll()

                // それ以外はログインが必要
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // ← 🔁 login.htmlへのGETは /login-page に変更
                .loginProcessingUrl("/login") // ← POSTは /login に飛ばす
                .defaultSuccessUrl("/tasks", true) // ← 成功後は /tasks にリダイレクト
                .failureUrl("/login?error") 
                .permitAll()
            )
            .logout(logout -> logout
            	    .logoutUrl("/logout")               // ← POST /logout を受ける
            	    .logoutSuccessUrl("/login?logout")  // ← ログアウト後は /login?logout
            	    .invalidateHttpSession(true)        // ← セッション無効化
            	    .deleteCookies("JSESSIONID")        // ← Cookieも削除
            	    .permitAll()
            );

        return http.build();
    }
}
