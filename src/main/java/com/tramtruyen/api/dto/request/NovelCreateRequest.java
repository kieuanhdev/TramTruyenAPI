package com.tramtruyen.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record NovelCreateRequest(

        @NotNull(message = "ID Thể loại không được để trống")
        Integer categoryId,

        @NotBlank(message = "Tên truyện không được để trống")
        String title,

        String summary,
        String coverUrl
) {}