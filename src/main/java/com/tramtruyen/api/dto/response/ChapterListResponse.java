package com.tramtruyen.api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChapterListResponse(
        UUID id,
        Integer chapterNo,
        String title,
        Boolean isPublished,
        LocalDateTime publishedAt
) {}