package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/* crawler 작동 시, 중복 news를 가져오는 순간 실행 멈추도록 uk 추가함 */
@Entity
@Table(name = "news", uniqueConstraints = {
    @UniqueConstraint(name = "uk_news_title", columnNames = "title"),
    @UniqueConstraint(name = "uk_news_reference", columnNames = "reference")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("뉴스 마스터 테이블")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("뉴스 ID")
    private Long id;

    @Column(nullable = false, length = 200)
    @Comment("제목")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("본문")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private NewsCategory category;

    @Column(nullable = false, length = 500)
    @Comment("출처")
    private String reference;

    @Column(nullable = false)
    @Comment("조회수")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Comment("좋아요 누적수")
    @Builder.Default
    private Long heartCount = 0L;

    @Column(nullable = false)
    @Comment("공유 누적수")
    @Builder.Default
    private Long shareCount = 0L;

    @Column(nullable = false)
    @Comment("신고 누적수")
    @Builder.Default
    private Long reportCount = 0L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime lastModifiedDate;

    // 비즈니스 메서드
    public static News createNews(String title, String content, String reference, NewsCategory category) {
        return News.builder()
                .title(title)
                .content(content)
                .reference(reference)
                .category(category != null ? category : NewsCategory.GENERAL)
                .viewCount(0L)
                .heartCount(0L)
                .shareCount(0L)
                .reportCount(0L)
                .build();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseHeartCount() {
        this.heartCount++;
    }

    public void decreaseHeartCount() {
        if (this.heartCount > 0) {
            this.heartCount--;
        }
    }

    public void increaseShareCount() { this.shareCount++; }

    public void increaseReportCount() {
        this.reportCount++;
    }
}
