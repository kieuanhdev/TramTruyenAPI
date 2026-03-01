package com.tramtruyen.api.controller;

import com.tramtruyen.api.service.NovelService;
import com.tramtruyen.api.dto.request.NovelCreateRequest;
import com.tramtruyen.api.dto.response.CoverUploadResponse;
import com.tramtruyen.api.dto.response.NovelResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/novels")
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    @PostMapping
    // CHỈ CHO PHÉP ROLE LÀ AUTHOR HOẶC ADMIN ĐƯỢC VÀO
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<NovelResponse> createNovel(@Valid @RequestBody NovelCreateRequest request) {
        NovelResponse response = novelService.createNovel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NovelResponse> getNovelById(@PathVariable UUID id) {
        NovelResponse response = novelService.getNovelById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-cover")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<CoverUploadResponse> uploadCover(@RequestParam("file") MultipartFile file) throws IOException {
        CoverUploadResponse response = novelService.uploadCover(file);
        return ResponseEntity.ok(response);
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