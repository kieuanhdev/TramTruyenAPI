package com.tramtruyen.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CommentCreateRequest(
        @NotBlank(message = "Nội dung bình luận không được để trống")
        @Size(max = 2000, message = "Nội dung tối đa 2000 ký tự")
        String content,

        UUID parentCommentId
) {}
