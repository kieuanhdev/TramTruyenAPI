package com.tramtruyen.api.presentation.controllers;

import com.tramtruyen.api.application.services.NovelService;
import com.tramtruyen.api.presentation.payloads.request.NovelCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.NovelResponse;
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
}