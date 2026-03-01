package com.tramtruyen.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        LocalDate dateOfBirth,
        String role,
        String status,
        LocalDateTime createdAt
) {}