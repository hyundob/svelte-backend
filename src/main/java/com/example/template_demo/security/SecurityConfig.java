package com.example.template_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/register").permitAll()  // 로그인·회원가입은 인증 없이 허용
                        .anyRequest().authenticated()  // 나머지는 인증 필요
                )
                .httpBasic(httpBasic -> httpBasic.disable());  // Basic 인증 비활성화

        return http.build();
    }
}
