package com.bugmaker.apt.dto.mypage;

import java.time.LocalDateTime;

/**
 * 마이페이지 활동 목록 응답 (뉴스, 포럼, 댓글, 지역 공통)
 * 각 위젯 클릭 시 실제 목록을 페이징으로 조회
 * 
 * Record 타입으로 불변 DTO 구현
 */
public record UserMyPageListResponse(
        Long id,
        String type,         // "news", "forum", "comment", "region", "report"
        String title,
        String content,
        String author,
        LocalDateTime createdAt,
        Long viewCount,
        Long likeCount,
        Long commentCount
) {
    /**
     * 뉴스 활동 아이템 생성
     */
    public static UserMyPageListResponse forNews(
            Long id,
            String title,
            String content,
            String reference,
            LocalDateTime createdAt,
            Long viewCount,
            Long likeCount
    ) {
        return new UserMyPageListResponse(
                id,
                "news",
                title,
                content,
                reference,
                createdAt,
                viewCount,
                likeCount,
                0L
        );
    }

    /**
     * 포럼 활동 아이템 생성
     */
    public static UserMyPageListResponse forForum(
            Long id,
            String title,
            String content,
            String authorNickname,
            LocalDateTime createdAt,
            Long viewCount,
            Long likeCount,
            Long commentCount
    ) {
        return new UserMyPageListResponse(
                id,
                "forum",
                title,
                content,
                authorNickname,
                createdAt,
                viewCount,
                likeCount,
                commentCount
        );
    }

    /**
     * 댓글 활동 아이템 생성
     */
    public static UserMyPageListResponse forComment(
            Long id,
            String title,
            String content,
            String authorNickname,
            LocalDateTime createdAt,
            Long likeCount
    ) {
        return new UserMyPageListResponse(
                id,
                "comment",
                title,
                content,
                authorNickname,
                createdAt,
                0L,
                likeCount,
                0L
        );
    }

    /**
     * 지역 활동 아이템 생성
     */
    public static UserMyPageListResponse forRegion(
            Long id,
            String regionName,
            Long viewCount,
            String change
    ) {
        return new UserMyPageListResponse(
                id,
                "region",
                regionName,
                "조회수 변화: " + change,
                null,
                null,
                viewCount,
                0L,
                0L
        );
    }

    /**
     * 신고 활동 아이템 생성
     */
    public static UserMyPageListResponse forReport(
            Long id,
            String targetType,
            String title,
            String content,
            String reporterNickname,
            LocalDateTime createdAt
    ) {
        return new UserMyPageListResponse(
                id,
                targetType,
                title,
                content,
                reporterNickname,
                createdAt,
                0L,
                0L,
                0L
        );
    }
}
