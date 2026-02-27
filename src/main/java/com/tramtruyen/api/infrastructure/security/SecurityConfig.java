package com.tramtruyen.api.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. Khai báo Bean băm mật khẩu (BCrypt là thuật toán chuẩn công nghiệp hiện nay)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Cấu hình phân luồng (Tạm thời mở toang để test mã hóa mật khẩu)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Tắt chống giả mạo request vì API dùng Token
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // TẠM THỜI cho phép tất cả đi qua mà không cần đăng nhập
                );

        return http.build();
    }
}