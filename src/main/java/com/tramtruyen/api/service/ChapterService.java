package com.tramtruyen.api.service;

import com.tramtruyen.api.model.ChapterEntity;
import com.tramtruyen.api.model.NovelEntity;
import com.tramtruyen.api.repository.ChapterRepository;
import com.tramtruyen.api.repository.NovelRepository;
import com.tramtruyen.api.dto.request.ChapterCreateRequest;
import com.tramtruyen.api.dto.response.ChapterListResponse;
import com.tramtruyen.api.dto.response.ChapterResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final NovelRepository novelRepository;

    @Transactional
    public ChapterResponse createChapter(UUID novelId, ChapterCreateRequest request) {

        // 1. Lấy danh tính người đang thao tác từ Token
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Kiểm tra bộ truyện có tồn tại không
        NovelEntity novel = novelRepository.findById(novelId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với ID này!"));

        // 3. CHỐT CHẶN BẢO MẬT: Kiểm tra quyền sở hữu
        // So sánh email của tác giả bộ truyện với email của người đang cầm Token
        if (!novel.getAuthor().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Lỗi 403: Bạn không có quyền đăng chương cho bộ truyện của người khác!");
        }

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

    // Không cần @Transactional vì chỉ đọc dữ liệu
    public PageResponse<ChapterListResponse> getChaptersByNovel(
            UUID novelId, int pageNo, int pageSize, String sortBy, String sortDir) {

        // 1. Kiểm tra truyện có tồn tại không (Tránh user truyền ID bậy bạ)
        if (!novelRepository.existsById(novelId)) {
            throw new RuntimeException("Không tìm thấy truyện với ID này!");
        }

        // 2. Cấu hình sắp xếp (Mặc định mục lục sẽ xếp theo chapterNo tăng dần - ASC)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. Lấy dữ liệu từ Database
        Page<ChapterEntity> chapterPage = chapterRepository.findByNovelId(novelId, pageable);

        // 4. Map Entity sang DTO siêu nhẹ (Bỏ qua content)
        List<ChapterListResponse> content = chapterPage.getContent().stream()
                .map(chapter -> new ChapterListResponse(
                        chapter.getId(),
                        chapter.getChapterNo(),
                        chapter.getTitle(),
                        chapter.getIsPublished(),
                        chapter.getPublishedAt()
                )).toList();

        // 5. Đóng gói và trả về
        return new PageResponse<>(
                content,
                chapterPage.getNumber(),
                chapterPage.getSize(),
                chapterPage.getTotalElements(),
                chapterPage.getTotalPages(),
                chapterPage.isLast()
        );
    }

    // PHẢI CÓ @Transactional vì chúng ta sẽ update cột view của Novel
    @Transactional
    public ChapterResponse getChapterDetail(UUID chapterId) {
        // 1. Tìm chương truyện theo ID
        ChapterEntity chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương truyện này!"));

        // 2. Lấy bộ truyện chứa chương này ra
        NovelEntity novel = chapter.getNovel();

        // 3. TĂNG VIEW: Cộng 1 vào tổng số lượt đọc hiện tại
        novel.setTotalViews(novel.getTotalViews() + 1);

        // 4. Lưu lại bộ truyện (Spring Data JPA sẽ tự động sinh lệnh UPDATE total_views)
        novelRepository.save(novel);

        // 5. Trả về toàn bộ thông tin chương (Bao gồm cục text nội dung siêu dài)
        return new ChapterResponse(
                chapter.getId(),
                novel.getId(),
                chapter.getChapterNo(),
                chapter.getTitle(),
                chapter.getContent(), // Trả về nội dung để độc giả đọc
                chapter.getIsPublished(),
                chapter.getPublishedAt()
        );
    }
}