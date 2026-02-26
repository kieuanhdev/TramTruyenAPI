package com.tramtruyen.api.presentation.payloads.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String fullName,
        String avatarUrl,
        String role,
        String status,
        LocalDateTime createdAt
) {}