package com.tramtruyen.api.application.services;

import com.tramtruyen.api.infrastructure.persistence.entity.UserEntity;
import com.tramtruyen.api.infrastructure.persistence.repository.UserRepository;
import com.tramtruyen.api.presentation.payloads.request.UserCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        // 1. Kiểm tra logic nghiệp vụ: Email đã tồn tại chưa?
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email này đã được sử dụng!");
            // Sau này chúng ta sẽ custom Exception sau cho mượt
        }

        // 2. Chuyển đổi từ DTO sang Entity (Dùng Builder chuẩn mà bạn đã viết)
        UserEntity newUser = UserEntity.builder()
                .email(request.email())
                // TODO: Sau này tích hợp Spring Security sẽ băm mật khẩu bằng BCrypt ở đây
                .passwordHash(request.password())
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
}