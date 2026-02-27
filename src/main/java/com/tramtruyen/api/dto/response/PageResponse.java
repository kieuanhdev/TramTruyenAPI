package com.tramtruyen.api.dto.response;

import java.util.List;

// Sử dụng Generic <T> để sau này có thể dùng cho cả Danh sách User, Danh sách Chapter...
public record PageResponse<T>(
        List<T> content,       // Danh sách dữ liệu thực tế (NovelResponse, UserResponse...)
        int pageNo,            // Trang hiện tại (Bắt đầu từ 0)
        int pageSize,          // Số lượng phần tử trên 1 trang
        long totalElements,    // Tổng số lượng tất cả phần tử trong DB
        int totalPages,        // Tổng số trang
        boolean isLast         // Có phải trang cuối cùng không? (Frontend dùng để ẩn nút "Next")
) {}