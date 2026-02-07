package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.domain.shared.CoreEntity;
import com.bugmaker.apt.enums.news.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * 날짜별 뉴스 다이제스트 (AI 요약본)
 */
@Entity
@Table(name = "news_digest",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_digest_date_category", columnNames = {"digest_date", "category"})
    },
    indexes = {
        @Index(name = "idx_digest_date", columnList = "digest_date"),
        @Index(name = "idx_digest_category", columnList = "category")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("날짜별 뉴스 다이제스트")
public class NewsDigest extends CoreEntity {

    @Column(name = "digest_date", nullable = false)
    @Comment("다이제스트 날짜")
    private LocalDate digestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Comment("카테고리")
    private NewsCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("AI 요약 내용")
    private String summary;

    @Column(name = "news_ids", columnDefinition = "TEXT")
    @Comment("포함된 뉴스 ID 목록 (JSON 배열)")
    private String newsIds;

    @Column(nullable = false)
    @Comment("포함된 뉴스 개수")
    @Builder.Default
    private Integer newsCount = 0;

    // 비즈니스 메서드
    public static NewsDigest create(LocalDate digestDate, NewsCategory category, String summary, String newsIds, Integer newsCount) {
        return NewsDigest.builder()
                .digestDate(digestDate)
                .category(category)
                .summary(summary)
                .newsIds(newsIds)
                .newsCount(newsCount)
                .build();
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }
}
