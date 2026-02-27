package com.tramtruyen.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
// Ràng buộc đa cột: Đảm bảo 1 truyện không thể có 2 chương trùng số thứ tự (chapterNo)
@Table(name = "chapters", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"novel_id", "chapter_no"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    // Quan hệ N-1: Nhiều Chương thuộc về 1 Truyện
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id", nullable = false)
    private NovelEntity novel;

    @Setter
    @Column(name = "chapter_no", nullable = false)
    private Integer chapterNo;

    @Setter
    @Column(nullable = false, length = 255)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished;

    @Setter
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Builder
    private ChapterEntity(NovelEntity novel, Integer chapterNo, String title, String content, Boolean isPublished, LocalDateTime publishedAt) {
        this.novel = novel;
        this.chapterNo = chapterNo;
        this.title = title;
        this.content = content;
        this.isPublished = isPublished != null ? isPublished : false; // Mặc định là lưu nháp
        this.publishedAt = publishedAt;
    }
}