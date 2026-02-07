package com.bugmaker.apt.contoller.news;

import com.bugmaker.apt.common.response.ApiResponse;
import com.bugmaker.apt.domain.news.DigestCreateRequest;
import com.bugmaker.apt.domain.news.DigestResponse;
import com.bugmaker.apt.domain.news.PendingNewsResponse;
import com.bugmaker.apt.service.news.NewsDigestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MCP 서버 전용 API
 * AI가 뉴스를 요약하고 저장하는 엔드포인트
 */
@Slf4j
@RestController
@RequestMapping("/api/mcp/digest")
@RequiredArgsConstructor
@Tag(name = "MCP Digest API", description = "AI 뉴스 요약 API (MCP 서버 전용)")
public class McpDigestController {

    private final NewsDigestService newsDigestService;

    @Value("${news.crawler.api-key}")
    private String expectedApiKey;

    /**
     * API Key 인증 인터셉터
     */
    @ModelAttribute
    public void validateApiKey(@RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            log.warn("[MCP] 인증 실패 - 유효하지 않은 API Key");
            throw new IllegalArgumentException("유효하지 않은 API Key입니다");
        }
    }

    /**
     * 1. 요약 대기 중인 뉴스 목록 조회
     * GET /api/mcp/digest/pending
     */
    @GetMapping("/pending")
    @Operation(summary = "요약 대기 중인 뉴스 조회", 
               description = "어제 날짜의 뉴스를 카테고리별로 그룹화하여 반환 (아직 요약 안 된 것만)")
    public ApiResponse<List<PendingNewsResponse>> getPendingNews() {
        List<PendingNewsResponse> result = newsDigestService.getPendingNews();
        return ApiResponse.ok(result);
    }

    /**
     * 2. AI 요약 저장
     * POST /api/mcp/digest
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "AI 요약 저장", 
               description = "날짜/카테고리별 뉴스 요약본 저장")
    public ApiResponse<DigestResponse> createDigest(@Valid @RequestBody DigestCreateRequest request) {
        DigestResponse response = newsDigestService.createDigest(request);
        return ApiResponse.created(response);
    }
}
