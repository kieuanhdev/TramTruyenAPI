package com.tramtruyen.api.application.services;

import com.tramtruyen.api.infrastructure.persistence.entity.ChapterEntity;
import com.tramtruyen.api.infrastructure.persistence.entity.NovelEntity;
import com.tramtruyen.api.infrastructure.persistence.repository.ChapterRepository;
import com.tramtruyen.api.infrastructure.persistence.repository.NovelRepository;
import com.tramtruyen.api.presentation.payloads.request.ChapterCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.ChapterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final NovelRepository novelRepository;

    @Transactional
    public ChapterResponse createChapter(UUID novelId, ChapterCreateRequest request) {
        // 1. Kiểm tra bộ truyện có tồn tại không
        NovelEntity novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với ID này!"));

        // 2. Kiểm tra trùng số thứ tự chương (Bảo vệ tính toàn vẹn dữ liệu)
        if (chapterRepository.existsByNovelAndChapterNo(novel, request.chapterNo())) {
            throw new RuntimeException("Chương " + request.chapterNo() + " đã tồn tại trong bộ truyện này!");
        }

        // 3. Xử lý logic thời gian xuất bản
        boolean isPublished = request.isPublished() != null ? request.isPublished() : false;
        LocalDateTime publishedAt = isPublished ? LocalDateTime.now() : null;

        // 4. Khởi tạo ChapterEntity
        ChapterEntity newChapter = ChapterEntity.builder()
                .novel(novel)
                .chapterNo(request.chapterNo())
                .title(request.title())
                .content(request.content())
                .isPublished(isPublished)
                .publishedAt(publishedAt)
                .build();

        // 5. Lưu xuống DB
        ChapterEntity savedChapter = chapterRepository.save(newChapter);

        // 6. Map sang Response
        return new ChapterResponse(
                savedChapter.getId(),
                savedChapter.getNovel().getId(),
                savedChapter.getChapterNo(),
                savedChapter.getTitle(),
                savedChapter.getContent(),
                savedChapter.getIsPublished(),
                savedChapter.getPublishedAt()
        );
    }
}