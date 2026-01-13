package com.bugmaker.apt.domain.news;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Python → Java
 * 뉴스 생성 요청 DTO
 * Python RSS 크롤러에서 사용
 */
public record NewsCreateRequest(
        
        @NotBlank(message = "제목은 필수입니다")
        @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
        String title,
        
        @NotBlank(message = "본문은 필수입니다")
        String content,
        
        @NotBlank(message = "출처는 필수입니다")
        @Size(max = 500, message = "출처는 500자를 초과할 수 없습니다")
        String reference
) {
    /**
     * DTO → Entity 변환
     */
    public News toEntity() {
        return News.builder()
                .title(title)
                .content(content)
                .reference(reference)
                .viewCount(0L)
                .heartCount(0L)
                .reportCount(0L)
                .build();
    }
}
