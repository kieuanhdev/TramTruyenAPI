package com.tramtruyen.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID userId,
        String userFullName,
        String userAvatarUrl,
        String content,
        LocalDateTime createdAt,
        UUID novelId,
        UUID chapterId,
        UUID parentCommentId,
        long likeCount,
        long dislikeCount,
        String userReaction,
        List<CommentResponse> replies
) {}
