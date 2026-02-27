package com.tramtruyen.api.controller;

import com.tramtruyen.api.security.JwtTokenProvider;
import com.tramtruyen.api.dto.request.LoginRequest; // Bạn tự tạo record này nhé (email, password)
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Spring Security sẽ tự động lấy email và raw password đi so sánh với cái hash trong DB
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        // 2. Nếu mật khẩu sai, Spring sẽ ném lỗi và văng ra ngay (ta có thể bắt lỗi 401 sau)
        // Nếu đúng, lưu thông tin xác thực vào Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gọi máy in Token
        String jwt = tokenProvider.generateToken(authentication);

        // 4. Trả Token về cho Frontend (Dùng Map tạm cho nhanh, bạn có thể tạo record AuthResponse cho chuẩn)
        return ResponseEntity.ok(Map.of(
                "accessToken", jwt,
                "tokenType", "Bearer"
        ));
    }
}