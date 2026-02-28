package com.tramtruyen.api.repository;

import com.tramtruyen.api.model.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, UUID> {

    Optional<CommentLikeEntity> findByCommentIdAndUserId(UUID commentId, UUID userId);

    long countByCommentIdAndType(UUID commentId, String type);
}
