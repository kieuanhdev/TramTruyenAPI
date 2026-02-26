package com.tramtruyen.api.infrastructure.persistence.repository;

import com.tramtruyen.api.infrastructure.persistence.entity.ChapterEntity;
import com.tramtruyen.api.infrastructure.persistence.entity.NovelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<ChapterEntity, UUID> {

    // Spring Data JPA tự động dịch tên hàm này thành câu SQL SELECT COUNT(...)
    boolean existsByNovelAndChapterNo(NovelEntity novel, Integer chapterNo);
}