package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.domain.shared.CoreEntity;
import com.bugmaker.apt.enums.news.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static io.jsonwebtoken.lang.Assert.state;

/* crawler 작동 시, 중복 news를 가져오는 순간 실행 멈추도록 uk 추가함 */
@Entity
@Table(name = "news", uniqueConstraints = {
    @UniqueConstraint(name = "uk_news_title", columnNames = "title"),
    @UniqueConstraint(name = "uk_news_reference", columnNames = "reference")
})
@Where(clause = "deleted = false")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("뉴스 테이블")
public class News extends CoreEntity {

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

    @Column(length = 1000)
    @Comment("썸네일 URL") // 썸네일 이미지 URL (목록용)
    private String thumbnailUrl;

    @Column(length = 1000)
    @Comment("상세 이미지 URL") // 상세 페이지 대표 이미지 URL
    private String detailImageUrl;

    @Column(nullable = false)
    @Comment("조회수")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Comment("좋아요 누적수")
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Comment("즐겨찾기 누적수")
    @Builder.Default
    private Long favoriteCount = 0L;

    @Column(nullable = false)
    @Comment("공유 누적수")
    @Builder.Default
    private Long shareCount = 0L;

    @Column(nullable = false)
    @Comment("신고 누적수")
    @Builder.Default
    private Long reportCount = 0L;

    // 비즈니스 메서드
    // 뉴스 생성(본문에 이미지 없는 뉴스일때)
    public static News createNews(String title, String content, String reference, NewsCategory category) {
        return News.builder()
                .title(title)
                .content(content)
                .reference(reference)
                .category(category != null ? category : NewsCategory.GENERAL)
                .build();
    }

    // 뉴스 생성(본문에 이미지 존재 시)
    public static News createNews(String title, String content, String reference, NewsCategory category, 
                                  String thumbnailUrl, String detailImageUrl) {
        return News.builder()
                .title(title)
                .content(content)
                .reference(reference)
                .category(category != null ? category : NewsCategory.GENERAL)
                .thumbnailUrl(thumbnailUrl)
                .detailImageUrl(detailImageUrl)
                .build();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseFavoriteCount() { this.favoriteCount++; }

    public void decreaseFavoriteCount() {
        state(this.favoriteCount > 0, "즐겨찾기는 음수가 될 수 없습니다.");
        this.favoriteCount--;
    }

    public void increaseShareCount() { this.shareCount++; }

    public void increaseReportCount() {
        this.reportCount++;
    }
}
