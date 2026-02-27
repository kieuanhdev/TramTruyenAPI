package com.tramtruyen.api.service;

import com.tramtruyen.api.model.CategoryEntity;
import com.tramtruyen.api.model.NovelEntity;
import com.tramtruyen.api.model.UserEntity;
import com.tramtruyen.api.repository.CategoryRepository;
import com.tramtruyen.api.repository.NovelRepository;
import com.tramtruyen.api.repository.UserRepository;
import com.tramtruyen.api.dto.request.NovelCreateRequest;
import com.tramtruyen.api.dto.response.NovelResponse;
import com.tramtruyen.api.dto.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NovelService {

    private final NovelRepository novelRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public NovelResponse createNovel(NovelCreateRequest request) {
        // 1. LẤY DANH TÍNH TỪ TOKEN (BẢO MẬT TUYỆT ĐỐI)
        // SecurityContextHolder chính là két sắt chứa thông tin người dùng đang gọi API
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Tìm Tác giả bằng Email (Không thể mạo danh được nữa)
        UserEntity author = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tác giả với Email này!"));

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
    public NovelResponse getNovelById(UUID id) {
        NovelEntity novel = novelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy truyện với ID: " + id));

        return new NovelResponse(
                novel.getId(),
                novel.getTitle(),
                novel.getAuthor().getFullName(),
                novel.getCategory().getName(),
                novel.getSummary(),
                novel.getCoverUrl(),
                novel.getStatus(),
                novel.getTotalViews(),
                novel.getCreatedAt()
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