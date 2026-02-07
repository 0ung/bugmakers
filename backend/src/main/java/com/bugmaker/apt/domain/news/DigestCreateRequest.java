package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.enums.news.NewsCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MCP - AI 요약 저장 요청
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DigestCreateRequest {
    
    @NotBlank(message = "다이제스트 날짜는 필수입니다")
    private String digestDate;  // "2026-02-07"
    
    @NotNull(message = "카테고리는 필수입니다")
    private NewsCategory category;
    
    @NotBlank(message = "요약 내용은 필수입니다")
    private String summary;
    
    @NotEmpty(message = "뉴스 ID 목록은 필수입니다")
    private List<Long> newsIds;
}
