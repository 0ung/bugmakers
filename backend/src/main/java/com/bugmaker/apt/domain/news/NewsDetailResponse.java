package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.news.NewsCategory;

import java.time.LocalDateTime;

/**
 * 뉴스 상세 응답 DTO
 * Frontend 뉴스 상세 페이지에서 사용
 */
public record NewsDetailResponse(
        Long id,
        String title,
        String content,
        String reference,
        String category,        // Enum name (영문): REAL_ESTATE, MARKET, POLICY...
        String displayName,     // 화면 표시명 (한글): 부동산, 시장, 정책...
        String detailImageUrl,
        Long viewCount,
        Long likeCount,
        Long shareCount,
        Long reportCount,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    /**
     * Entity → DTO 변환 (상세용)
     */
    public static NewsDetailResponse from(News news) {
        NewsCategory newsCategory = news.getCategory() != null ? news.getCategory() : NewsCategory.GENERAL;
        
        return new NewsDetailResponse(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getReference(),
                newsCategory.name(),           // REAL_ESTATE
                newsCategory.getDisplayName(), // 부동산
                news.getDetailImageUrl(),
                news.getViewCount(),
                news.getLikeCount(),
                news.getShareCount(),
                news.getReportCount(),
                news.getCreatedDate(),
                news.getLastModifiedDate()
        );
    }
}
