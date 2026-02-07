package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.news.NewsCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MCP - 다이제스트 조회 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigestResponse {
    private Long id;
    private String digestDate;  // "2026-02-07"
    private NewsCategory category;
    private String summary;
    private List<Long> newsIds;
    private Integer newsCount;
    private LocalDateTime createdAt;
}
