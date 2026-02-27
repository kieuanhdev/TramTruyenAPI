package com.tramtruyen.api.repository;

import com.tramtruyen.api.model.ChapterEntity;
import com.tramtruyen.api.model.NovelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChapterRepository extends JpaRepository<ChapterEntity, UUID> {

    // Spring Data JPA tự động dịch tên hàm này thành câu SQL SELECT COUNT(...)
    boolean existsByNovelAndChapterNo(NovelEntity novel, Integer chapterNo);

    // Thêm hàm này: Tìm tất cả các chương thuộc về 1 bộ truyện cụ thể (Có phân trang)
    Page<ChapterEntity> findByNovelId(UUID novelId, Pageable pageable);
}