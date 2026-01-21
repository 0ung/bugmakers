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
        String category,
        String thumbnailUrl,
        Long viewCount,
        Long heartCount,
        Long shareCount,
        LocalDateTime createdDate
) {
    /**
     * Entity → DTO 변환 (목록용)
     */
    public static NewsResponse from(News news) {
        return new NewsResponse(
                news.getId(),
                news.getTitle(),
                news.getReference(),
                news.getCategory() != null ? news.getCategory().getDisplayName() : NewsCategory.GENERAL.getDisplayName(),
                news.getThumbnailUrl(),
                news.getViewCount(),
                news.getHeartCount(),
                news.getShareCount(),
                news.getCreatedDate()
        );
    }
}
