package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.NewsCategory;

import java.time.LocalDateTime;

/**
 * Java → Frontend
 * 뉴스 목록 응답 DTO
 * Frontend 뉴스 목록 페이지에서 사용
 */
public record NewsResponse(
        Long id,
        String title,
        String reference,
        String category,        // Enum name (영문): REAL_ESTATE, MARKET, POLICY...
        String displayName,     // 화면 표시명 (한글): 부동산, 시장, 정책...
        String thumbnailUrl,
        Long viewCount,
        Long likeCount,
        Long shareCount,
        LocalDateTime createdDate
) {
    /**
     * Entity → DTO 변환 (목록용)
     */
    public static NewsResponse from(News news) {
        NewsCategory newsCategory = news.getCategory() != null ? news.getCategory() : NewsCategory.GENERAL;
        
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getReference(),
                newsCategory.name(),           // REAL_ESTATE
                newsCategory.getDisplayName(), // 부동산
                news.getThumbnailUrl(),
                news.getViewCount(),
                news.getLikeCount(),
                news.getShareCount(),
                news.getCreatedDate()
        );
    }
}
