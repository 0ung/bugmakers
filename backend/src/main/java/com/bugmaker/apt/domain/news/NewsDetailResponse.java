package com.bugmaker.apt.domain.news;

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
        Long viewCount,
        Long heartCount,
        Long reportCount,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    /**
     * Entity → DTO 변환 (상세용)
     */
    public static NewsDetailResponse from(News news) {
        return new NewsDetailResponse(
                news.getId(),
                news.getTitle(),
                news.getContent(),
                news.getReference(),
                news.getViewCount(),
                news.getHeartCount(),
                news.getReportCount(),
                news.getCreatedDate(),
                news.getLastModifiedDate()
        );
    }
}
