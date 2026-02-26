package com.tramtruyen.api.presentation.payloads.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,          // Mã lỗi HTTP (400, 404, 500...)
        String message,      // Lời nhắn lỗi ngắn gọn cho user hiểu
        LocalDateTime timestamp // Thời gian xảy ra lỗi
) {}