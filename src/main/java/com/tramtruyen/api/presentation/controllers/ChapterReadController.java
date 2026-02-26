package com.tramtruyen.api.presentation.controllers;

import com.tramtruyen.api.application.services.ChapterService;
import com.tramtruyen.api.presentation.payloads.response.ChapterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
// Chú ý URL: Truy cập trực tiếp vào chương truyện
@RequestMapping("/api/v1/chapters")
@RequiredArgsConstructor
public class ChapterReadController {

    private final ChapterService chapterService;

    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> getChapterDetail(@PathVariable UUID id) {
        ChapterResponse response = chapterService.getChapterDetail(id);
        return ResponseEntity.ok(response);
    }
}