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
    public void validateApiKey(@RequestHeader(value = "X-API-Key", required = true) String apiKey) {
        log.info("[MCP] Received API Key: '{}'", apiKey);
        log.info("[MCP] Expected API Key: '{}'", expectedApiKey);

        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("[MCP] 인증 실패 - API Key가 비어있음");
            throw new IllegalArgumentException("API Key가 필요합니다");
        }

        if (!expectedApiKey.equals(apiKey.trim())) {
            log.warn("[MCP] 인증 실패 - API Key 불일치");
            log.warn("[MCP] Received: '{}' (length: {})", apiKey, apiKey.length());
            log.warn("[MCP] Expected: '{}' (length: {})", expectedApiKey, expectedApiKey.length());
            throw new IllegalArgumentException("유효하지 않은 API Key입니다");
        }

        log.info("[MCP] ✅ API Key 인증 성공");
    }

    @GetMapping("/pending")
    @Operation(summary = "요약 대기 중인 뉴스 조회")
    public ApiResponse<List<PendingNewsResponse>> getPendingNews() {
        List<PendingNewsResponse> result = newsDigestService.getPendingNews();
        return ApiResponse.ok(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "AI 요약 저장")
    public ApiResponse<DigestResponse> createDigest(@Valid @RequestBody DigestCreateRequest request) {
        DigestResponse response = newsDigestService.createDigest(request);
        return ApiResponse.created(response);
    }
}