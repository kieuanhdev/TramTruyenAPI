package com.tramtruyen.api.presentation.payloads.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        String avatarUrl
) {}