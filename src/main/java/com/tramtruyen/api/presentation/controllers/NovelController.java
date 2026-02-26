package com.tramtruyen.api.presentation.controllers;

import com.tramtruyen.api.application.services.NovelService;
import com.tramtruyen.api.presentation.payloads.request.NovelCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.NovelResponse;
import com.tramtruyen.api.presentation.payloads.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    @PostMapping
    public ResponseEntity<NovelResponse> createNovel(@Valid @RequestBody NovelCreateRequest request) {
        NovelResponse response = novelService.createNovel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<NovelResponse>> getAllNovels(
            // Dùng @RequestParam để lấy tham số từ URL, gán luôn giá trị mặc định để tránh lỗi
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    ) {
        PageResponse<NovelResponse> response = novelService.getAllNovels(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}