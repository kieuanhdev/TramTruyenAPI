package com.tramtruyen.api.application.services;

import com.tramtruyen.api.infrastructure.persistence.entity.UserEntity;
import com.tramtruyen.api.infrastructure.persistence.repository.UserRepository;
import com.tramtruyen.api.presentation.payloads.request.UserCreateRequest;
import com.tramtruyen.api.presentation.payloads.request.UserUpdateRequest;
import com.tramtruyen.api.presentation.payloads.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 1. Kiểm tra logic nghiệp vụ: Email đã tồn tại chưa?
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email này đã được sử dụng!");
            // Sau này chúng ta sẽ custom Exception sau cho mượt
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        // 2. Chuyển đổi từ DTO sang Entity (Dùng Builder chuẩn mà bạn đã viết)
        UserEntity newUser = UserEntity.builder()
                .email(request.email())
                // TODO: Sau này tích hợp Spring Security sẽ băm mật khẩu bằng BCrypt ở đây
                .passwordHash(hashedPassword)
                .fullName(request.fullName())
                .avatarUrl(request.avatarUrl())
                .build();

        // 3. Lưu xuống Database
        UserEntity savedUser = userRepository.save(newUser);

        // 4. Map Entity ngược lại thành DTO để trả về Frontend an toàn
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getAvatarUrl(),
                savedUser.getRole(),
                savedUser.getStatus(),
                savedUser.getCreatedAt()
        );
    }

    // Không cần @Transactional ở đây vì ta chỉ ĐỌC dữ liệu (Read-only), giúp tăng hiệu năng
    public UserResponse getUserById(UUID id) {
        // 1. Tìm user trong DB, nếu không có thì ném ra lỗi
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Map Entity sang DTO để giấu passwordHash
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        // 1. Tìm user trong DB
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));

        // 2. Cập nhật thông tin (Chỉ gọi được các hàm set mà ta đã cho phép ở Entity)
        user.setFullName(request.fullName());
        user.setAvatarUrl(request.avatarUrl());

        // 3. Lưu vào DB (JPA sẽ tự động tạo câu lệnh UPDATE thay vì INSERT vì user đã có sẵn ID)
        UserEntity updatedUser = userRepository.save(user);

        // 4. Trả về dữ liệu mới
        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFullName(),
                updatedUser.getAvatarUrl(),
                updatedUser.getRole(),
                updatedUser.getStatus(),
                updatedUser.getCreatedAt()
        );
    }
}