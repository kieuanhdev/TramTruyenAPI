package com.tramtruyen.api.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
// JPA bắt buộc có NoArgsConstructor, dùng PROTECTED để ngăn code bên ngoài gọi bậy bạ
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Chỉ dùng ID để so sánh 2 Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Setter
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Setter
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Builder gắn thẳng vào constructor này để ÉP người code phải truyền những trường bắt buộc
    // Đồng thời gán luôn giá trị mặc định cho role và status ở đây cực kỳ an toàn
    @Builder
    private UserEntity(String email, String passwordHash, String fullName, String avatarUrl) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.role = "READER";   // Khởi tạo mặc định chuẩn xác
        this.status = "ACTIVE"; // Khởi tạo mặc định chuẩn xác
    }
}