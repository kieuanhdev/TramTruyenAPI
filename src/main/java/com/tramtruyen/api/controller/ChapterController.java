package com.tramtruyen.api.controller;

import com.tramtruyen.api.service.ChapterService;
import com.tramtruyen.api.dto.request.ChapterCreateRequest;
import com.tramtruyen.api.dto.response.ChapterListResponse;
import com.tramtruyen.api.dto.response.ChapterResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
// Chú ý URL: Thể hiện rõ Chapter là tài nguyên con của Novel
@RequestMapping("/api/v1/novels/{novelId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<ChapterResponse> createChapter(
            @PathVariable UUID novelId,
            @Valid @RequestBody ChapterCreateRequest request) {

        ChapterResponse response = chapterService.createChapter(novelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ChapterListResponse>> getChaptersByNovel(
            @PathVariable UUID novelId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "size", defaultValue = "50", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "chapterNo", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageResponse<ChapterListResponse> response =
                chapterService.getChaptersByNovel(novelId, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}