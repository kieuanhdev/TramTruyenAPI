package com.tramtruyen.api.service;

import com.tramtruyen.api.model.ChapterEntity;
import com.tramtruyen.api.model.CommentEntity;
import com.tramtruyen.api.model.CommentLikeEntity;
import com.tramtruyen.api.model.NovelEntity;
import com.tramtruyen.api.model.UserEntity;
import com.tramtruyen.api.repository.ChapterRepository;
import com.tramtruyen.api.repository.CommentLikeRepository;
import com.tramtruyen.api.repository.CommentRepository;
import com.tramtruyen.api.repository.NovelRepository;
import com.tramtruyen.api.repository.UserRepository;
import com.tramtruyen.api.dto.request.CommentCreateRequest;
import com.tramtruyen.api.dto.response.CommentResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public CommentResponse createCommentForNovel(UUID novelId, CommentCreateRequest request) {
        UserEntity user = getCurrentUser();
        NovelEntity novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với ID này!"));
        return createComment(user, novel, null, request);
    }

    @Transactional
    public CommentResponse createCommentForChapter(UUID chapterId, CommentCreateRequest request) {
        UserEntity user = getCurrentUser();
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương truyện này!"));
        return createComment(user, null, chapter, request);
    }

    private CommentResponse createComment(UserEntity user, NovelEntity novel, ChapterEntity chapter,
                                          CommentCreateRequest request) {
        CommentEntity parentComment = null;
        NovelEntity targetNovel = novel;
        ChapterEntity targetChapter = chapter;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận cha!"));
            if (parentComment.getParentComment() != null) {
                throw new RuntimeException("Chỉ được phản hồi trực tiếp bình luận gốc!");
            }
            targetNovel = parentComment.getNovel();
            targetChapter = parentComment.getChapter();
        }

        CommentEntity comment = CommentEntity.builder()
                .user(user)
                .novel(targetNovel)
                .chapter(targetChapter)
                .parentComment(parentComment)
                .content(request.content().trim())
                .build();

        CommentEntity saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    public PageResponse<CommentResponse> getCommentsByNovel(UUID novelId, int pageNo, int pageSize) {
        if (!novelRepository.existsById(novelId)) {
            throw new RuntimeException("Không tìm thấy truyện với ID này!");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<CommentEntity> page = commentRepository.findByNovelIdAndParentCommentIsNull(novelId, pageable);
        return buildPageResponse(page);
    }

    public PageResponse<CommentResponse> getCommentsByChapter(UUID chapterId, int pageNo, int pageSize) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new RuntimeException("Không tìm thấy chương truyện này!");
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<CommentEntity> page = commentRepository.findByChapterIdAndParentCommentIsNull(chapterId, pageable);
        return buildPageResponse(page);
    }

    @Transactional
    public CommentResponse updateComment(UUID commentId, String newContent) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận này!"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa bình luận này!");
        }
        comment.setContent(newContent.trim());
        CommentEntity saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận này!"));
        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này!");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse toggleLike(UUID commentId) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận này!"));
        Optional<CommentLikeEntity> existing = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (existing.isPresent()) {
            CommentLikeEntity like = existing.get();
            if ("LIKE".equals(like.getType())) {
                commentLikeRepository.delete(like);
            } else {
                like.setType("LIKE");
                commentLikeRepository.save(like);
            }
        } else {
            CommentLikeEntity like = CommentLikeEntity.builder()
                    .user(user)
                    .comment(comment)
                    .type("LIKE")
                    .build();
            commentLikeRepository.save(like);
        }
        return mapToResponse(comment);
    }

    @Transactional
    public CommentResponse toggleDislike(UUID commentId) {
        UserEntity user = getCurrentUser();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận này!"));
        Optional<CommentLikeEntity> existing = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        if (existing.isPresent()) {
            CommentLikeEntity like = existing.get();
            if ("DISLIKE".equals(like.getType())) {
                commentLikeRepository.delete(like);
            } else {
                like.setType("DISLIKE");
                commentLikeRepository.save(like);
            }
        } else {
            CommentLikeEntity like = CommentLikeEntity.builder()
                    .user(user)
                    .comment(comment)
                    .type("DISLIKE")
                    .build();
            commentLikeRepository.save(like);
        }
        return mapToResponse(comment);
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
    }

    private Optional<UserEntity> getCurrentUserOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    private PageResponse<CommentResponse> buildPageResponse(Page<CommentEntity> page) {
        List<CommentResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private CommentResponse mapToResponse(CommentEntity c) {
        long likeCount = commentLikeRepository.countByCommentIdAndType(c.getId(), "LIKE");
        long dislikeCount = commentLikeRepository.countByCommentIdAndType(c.getId(), "DISLIKE");
        String userReaction = getCurrentUserOptional()
                .flatMap(u -> commentLikeRepository.findByCommentIdAndUserId(c.getId(), u.getId()))
                .map(CommentLikeEntity::getType)
                .orElse(null);

        List<CommentResponse> replies = List.of();
        if (c.getParentComment() == null) {
            List<CommentEntity> replyList = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(c.getId());
            replies = replyList.stream().map(this::mapToResponseSimple).toList();
        }
        return new CommentResponse(
                c.getId(),
                c.getUser().getId(),
                c.getUser().getFullName(),
                c.getUser().getAvatarUrl(),
                c.getContent(),
                c.getCreatedAt(),
                c.getNovel() != null ? c.getNovel().getId() : null,
                c.getChapter() != null ? c.getChapter().getId() : null,
                c.getParentComment() != null ? c.getParentComment().getId() : null,
                likeCount,
                dislikeCount,
                userReaction,
                replies
        );
    }

    private CommentResponse mapToResponseSimple(CommentEntity c) {
        long likeCount = commentLikeRepository.countByCommentIdAndType(c.getId(), "LIKE");
        long dislikeCount = commentLikeRepository.countByCommentIdAndType(c.getId(), "DISLIKE");
        String userReaction = getCurrentUserOptional()
                .flatMap(u -> commentLikeRepository.findByCommentIdAndUserId(c.getId(), u.getId()))
                .map(CommentLikeEntity::getType)
                .orElse(null);
        return new CommentResponse(
                c.getId(),
                c.getUser().getId(),
                c.getUser().getFullName(),
                c.getUser().getAvatarUrl(),
                c.getContent(),
                c.getCreatedAt(),
                c.getNovel() != null ? c.getNovel().getId() : null,
                c.getChapter() != null ? c.getChapter().getId() : null,
                c.getParentComment() != null ? c.getParentComment().getId() : null,
                likeCount,
                dislikeCount,
                userReaction,
                List.of()
        );
    }
}
