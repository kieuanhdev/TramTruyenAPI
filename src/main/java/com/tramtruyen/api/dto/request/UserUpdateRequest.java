package com.tramtruyen.api.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        String avatarUrl,

        LocalDate dateOfBirth
) {}