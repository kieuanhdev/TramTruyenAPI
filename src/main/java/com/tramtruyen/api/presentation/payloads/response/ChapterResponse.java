package com.tramtruyen.api.presentation.payloads.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChapterResponse(
        UUID id,
        UUID novelId, // Trả về ID của truyện để Frontend dễ map
        Integer chapterNo,
        String title,
        String content,
        Boolean isPublished,
        LocalDateTime publishedAt
) {}