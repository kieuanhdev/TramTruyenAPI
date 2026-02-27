package com.tramtruyen.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "novels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NovelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    // Quan hệ N-1: Nhiều Truyện thuộc về 1 Tác giả
    // Không để @Setter ở đây vì tạo truyện xong không được đổi tác giả
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    // Quan hệ N-1: Nhiều Truyện có chung 1 Thể loại
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Setter
    @Column(nullable = false, length = 255)
    private String title;

    @Setter
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Setter
    @Column(nullable = false, length = 50)
    private String status;

    @Setter
    @Column(name = "total_views", nullable = false)
    private Long totalViews;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Quan hệ 1-N: 1 Truyện có Nhiều Chương
    // orphanRemoval = true: Nếu xóa chương khỏi List này, Hibernate sẽ tự xóa luôn trong DB
    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterEntity> chapters = new ArrayList<>();

    @Builder
    private NovelEntity(UserEntity author, CategoryEntity category, String title, String coverUrl, String summary) {
        this.author = author;
        this.category = category;
        this.title = title;
        this.coverUrl = coverUrl;
        this.summary = summary;
        this.status = "ONGOING"; // Trạng thái mặc định
        this.totalViews = 0L;    // Khởi tạo view bằng 0
    }
}