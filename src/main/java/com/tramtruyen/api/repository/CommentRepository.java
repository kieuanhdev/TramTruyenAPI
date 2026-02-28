package com.tramtruyen.api.repository;

import com.tramtruyen.api.model.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    Page<CommentEntity> findByNovelIdAndParentCommentIsNull(UUID novelId, Pageable pageable);

    Page<CommentEntity> findByChapterIdAndParentCommentIsNull(UUID chapterId, Pageable pageable);

    List<CommentEntity> findByParentCommentIdOrderByCreatedAtAsc(UUID parentCommentId);
}
