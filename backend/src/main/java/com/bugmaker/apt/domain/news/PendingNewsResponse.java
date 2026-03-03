package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.news.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP - 요약 대기 중인 뉴스 목록 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingNewsResponse {
    private String digestDate;  // "2026-02-07"
    private NewsCategory category;
    private Integer newsCount;
    private List<NewsItem> newsList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewsItem {
        private Long id;
        private String title;
        private String content;
        private String reference;
    }
}
