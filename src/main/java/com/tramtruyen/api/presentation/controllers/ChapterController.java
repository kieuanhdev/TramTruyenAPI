package com.tramtruyen.api.presentation.controllers;

import com.tramtruyen.api.application.services.ChapterService;
import com.tramtruyen.api.presentation.payloads.request.ChapterCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.ChapterListResponse;
import com.tramtruyen.api.presentation.payloads.response.ChapterResponse;
import com.tramtruyen.api.presentation.payloads.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
// Chú ý URL: Thể hiện rõ Chapter là tài nguyên con của Novel
@RequestMapping("/api/v1/novels/{novelId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
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