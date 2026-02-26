package com.tramtruyen.api.application.services;

import com.tramtruyen.api.infrastructure.persistence.entity.CategoryEntity;
import com.tramtruyen.api.infrastructure.persistence.entity.NovelEntity;
import com.tramtruyen.api.infrastructure.persistence.entity.UserEntity;
import com.tramtruyen.api.infrastructure.persistence.repository.CategoryRepository;
import com.tramtruyen.api.infrastructure.persistence.repository.NovelRepository;
import com.tramtruyen.api.infrastructure.persistence.repository.UserRepository;
import com.tramtruyen.api.presentation.payloads.request.NovelCreateRequest;
import com.tramtruyen.api.presentation.payloads.response.NovelResponse;
import com.tramtruyen.api.presentation.payloads.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public NovelResponse createNovel(NovelCreateRequest request) {
        // 1. Kiểm tra Tác giả có tồn tại không
        UserEntity author = userRepository.findById(request.authorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với ID này!"));

        // 2. Kiểm tra Thể loại có tồn tại không
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại với ID này!"));

        // 3. Khởi tạo NovelEntity (Lưu ý: Truyền thẳng object author và category vào)
        NovelEntity novel = NovelEntity.builder()
                .author(author)
                .category(category)
                .title(request.title())
                .summary(request.summary())
                .coverUrl(request.coverUrl())
                .build();

        // 4. Lưu xuống Database
        NovelEntity savedNovel = novelRepository.save(novel);

        // 5. Trả về Response
        return new NovelResponse(
                savedNovel.getId(),
                savedNovel.getTitle(),
                savedNovel.getAuthor().getFullName(), // Lấy tên tác giả
                savedNovel.getCategory().getName(),   // Lấy tên thể loại
                savedNovel.getSummary(),
                savedNovel.getCoverUrl(),
                savedNovel.getStatus(),
                savedNovel.getTotalViews(),
                savedNovel.getCreatedAt()
        );
    }

    // Chỉ đọc dữ liệu nên không cần @Transactional
    public PageResponse<NovelResponse> getAllNovels(int pageNo, int pageSize, String sortBy, String sortDir) {
        // 1. Tạo đối tượng sắp xếp (Tăng dần ASC hoặc Giảm dần DESC)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Cấu hình Phân trang
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. Gọi Repository (Spring JPA tự động sinh câu lệnh SQL có LIMIT và OFFSET)
        Page<NovelEntity> novelPage = novelRepository.findAll(pageable);

        // 4. Map danh sách Entity sang DTO
        List<NovelResponse> content = novelPage.getContent().stream()
                .map(novel -> new NovelResponse(
                        novel.getId(),
                        novel.getTitle(),
                        novel.getAuthor().getFullName(),
                        novel.getCategory().getName(),
                        novel.getSummary(),
                        novel.getCoverUrl(),
                        novel.getStatus(),
                        novel.getTotalViews(),
                        novel.getCreatedAt()
                )).toList();

        // 5. Đóng gói vào PageResponse và trả về
        return new PageResponse<>(
                content,
                novelPage.getNumber(),
                novelPage.getSize(),
                novelPage.getTotalElements(),
                novelPage.getTotalPages(),
                novelPage.isLast()
        );
    }
}