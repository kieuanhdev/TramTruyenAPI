package com.tramtruyen.api.repository;

import com.tramtruyen.api.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    // JPA tự động sinh ra câu query SQL tìm user theo email
    Optional<UserEntity> findByEmail(String email);

    // Dùng để kiểm tra email đã tồn tại chưa lúc đăng ký
    boolean existsByEmail(String email);
}