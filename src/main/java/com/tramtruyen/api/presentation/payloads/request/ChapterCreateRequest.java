package com.tramtruyen.api.presentation.payloads.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChapterCreateRequest(
        @NotNull(message = "Số thứ tự chương không được để trống")
        @Min(value = 1, message = "Số thứ tự chương phải từ 1 trở lên")
        Integer chapterNo,

        @NotBlank(message = "Tiêu đề chương không được để trống")
        String title,

        @NotBlank(message = "Nội dung chương không được để trống")
        String content,

        Boolean isPublished // Có xuất bản ngay không hay lưu nháp?
) {}