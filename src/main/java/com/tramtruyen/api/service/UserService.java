package com.tramtruyen.api.service;

import com.tramtruyen.api.model.UserEntity;
import com.tramtruyen.api.repository.UserRepository;
import com.tramtruyen.api.dto.request.UserCreateRequest;
import com.tramtruyen.api.dto.request.UserUpdateRequest;
import com.tramtruyen.api.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.base-path:./uploads}")
    private String uploadBasePath;

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
                .passwordHash(hashedPassword)
                .fullName(request.fullName())
                .avatarUrl(request.avatarUrl())
                .dateOfBirth(request.dateOfBirth())
                .build();

        UserEntity savedUser = userRepository.save(newUser);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getAvatarUrl(),
                savedUser.getDateOfBirth(),
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
                user.getDateOfBirth(),
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

        user.setFullName(request.fullName());
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl().isBlank() ? null : request.avatarUrl().trim());
        }
        if (request.dateOfBirth() != null) {
            user.setDateOfBirth(request.dateOfBirth());
        }

        UserEntity updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getFullName(),
                updatedUser.getAvatarUrl(),
                updatedUser.getDateOfBirth(),
                updatedUser.getRole(),
                updatedUser.getStatus(),
                updatedUser.getCreatedAt()
        );
    }

    // Lấy thông tin người dùng hiện tại từ Token
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại!"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getAvatarUrl(),
                user.getDateOfBirth(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        UserEntity user = userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại!"));
        user.setFullName(request.fullName().trim());
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl().isBlank() ? null : request.avatarUrl().trim());
        }
        user.setDateOfBirth(request.dateOfBirth());
        UserEntity updated = userRepository.save(user);
        return new UserResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getFullName(),
                updated.getAvatarUrl(),
                updated.getDateOfBirth(),
                updated.getRole(),
                updated.getStatus(),
                updated.getCreatedAt()
        );
    }

    @Transactional
    public UserResponse uploadAvatar(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ảnh để tải lên!");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new RuntimeException("Kích thước ảnh tối đa 5MB!");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new RuntimeException("Chỉ chấp nhận ảnh: JPEG, PNG, GIF, WebP!");
        }

        UserEntity user = userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại!"));

        String ext = getExtension(contentType);
        String filename = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path uploadDir = Paths.get(uploadBasePath, "avatars").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath.toFile());

        String avatarUrl = "/uploads/avatars/" + filename;
        user.setAvatarUrl(avatarUrl);
        UserEntity updated = userRepository.save(user);

        return new UserResponse(
                updated.getId(),
                updated.getEmail(),
                updated.getFullName(),
                updated.getAvatarUrl(),
                updated.getDateOfBirth(),
                updated.getRole(),
                updated.getStatus(),
                updated.getCreatedAt()
        );
    }

    private String getExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}