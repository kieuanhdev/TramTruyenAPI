package com.tramtruyen.api.presentation.payloads.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record NovelResponse(
        UUID id,
        String title,
        String authorName,   // Lấy từ UserEntity
        String categoryName, // Lấy từ CategoryEntity
        String summary,
        String coverUrl,
        String status,
        Long totalViews,
        LocalDateTime createdAt
) {}