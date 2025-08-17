// src/main/java/com/example/demo/config/JpaConfig.java
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // ★ @CreatedDate / @LastModifiedDate を有効化
public class JpaConfig {}
