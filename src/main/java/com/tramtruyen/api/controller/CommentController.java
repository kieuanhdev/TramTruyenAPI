package com.tramtruyen.api.controller;

import com.tramtruyen.api.service.CommentService;
import com.tramtruyen.api.dto.request.CommentCreateRequest;
import com.tramtruyen.api.dto.response.CommentResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/v1/novels/{novelId}/comments")
    public ResponseEntity<CommentResponse> createCommentForNovel(
            @PathVariable UUID novelId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createCommentForNovel(novelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/novels/{novelId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> getCommentsByNovel(
            @PathVariable UUID novelId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        PageResponse<CommentResponse> response = commentService.getCommentsByNovel(novelId, pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/chapters/{chapterId}/comments")
    public ResponseEntity<CommentResponse> createCommentForChapter(
            @PathVariable UUID chapterId,
            @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse response = commentService.createCommentForChapter(chapterId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/chapters/{chapterId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> getCommentsByChapter(
            @PathVariable UUID chapterId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        PageResponse<CommentResponse> response = commentService.getCommentsByChapter(chapterId, pageNo, pageSize);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/v1/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String content = body != null ? body.get("content") : null;
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Nội dung không được để trống!");
        }
        CommentResponse response = commentService.updateComment(id, content);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/v1/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/comments/{id}/like")
    public ResponseEntity<CommentResponse> toggleLike(@PathVariable UUID id) {
        CommentResponse response = commentService.toggleLike(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/v1/comments/{id}/dislike")
    public ResponseEntity<CommentResponse> toggleDislike(@PathVariable UUID id) {
        CommentResponse response = commentService.toggleDislike(id);
        return ResponseEntity.ok(response);
    }
}
