package com.bugmaker.apt.contoller.news;

import com.bugmaker.apt.domain.news.NewsCreateRequest;
import com.bugmaker.apt.domain.news.NewsDetailResponse;
import com.bugmaker.apt.domain.news.NewsResponse;
import com.bugmaker.apt.service.news.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 뉴스 컨트롤러
 * RSS 크롤링 데이터 관리 및 뉴스 조회 API
 */
@Tag(name = "News", description = "뉴스 API")
@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Value("${news.crawler.api-key}")
    private String crawlerApiKey;

    /**
     * 뉴스 등록 (Python 크롤러 전용)
     * API Key 인증 필요
     * 
     * POST /api/news
     * Header: X-API-Key: {api-key}
     * 
     * @param apiKey API 인증 키
     * @param request 뉴스 생성 요청
     * @return 생성된 뉴스 정보
     */
    @Operation(
            summary = "뉴스 등록",
            description = "RSS 크롤러가 수집한 뉴스를 등록합니다. (Python 크롤러 전용)",
            security = @SecurityRequirement(name = "X-API-Key")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "뉴스 등록 성공"),
            @ApiResponse(responseCode = "400", description = "중복된 뉴스"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
            @Parameter(description = "API 인증 키", required = true)
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @Valid @RequestBody NewsCreateRequest request) {

        log.info("뉴스 등록 요청 - 제목: {}", request.title());

        // API Key 검증
        if (apiKey == null || !apiKey.equals(crawlerApiKey)) {
            log.warn("유효하지 않은 API Key - 요청 거부");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        NewsResponse response = newsService.createNews(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 뉴스 목록 조회 (페이징)
     * 최신순 정렬
     * 
     * GET /api/news?page=0&size=10
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 뉴스 목록
     */
    @Operation(
            summary = "뉴스 목록 조회",
            description = "최신순으로 정렬된 뉴스 목록을 페이징하여 조회합니다."
    )
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNewsList(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size) {

        log.info("뉴스 목록 조회 - 페이지: {}, 사이즈: {}", page, size);

        // 최신순 정렬 (createdDate DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<NewsResponse> newsPage = newsService.getNewsList(pageable);

        return ResponseEntity.ok(newsPage);
    }

    /**
     * 뉴스 상세 조회
     * 
     * GET /api/news/{id}
     * 
     * @param id 뉴스 ID
     * @return 뉴스 상세 정보
     */
    @Operation(summary = "뉴스 상세 조회", description = "특정 뉴스의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(@Parameter(description = "뉴스 ID")@PathVariable Long id) {
        log.info("뉴스 상세 조회 - ID: {}", id);

        NewsDetailResponse response = newsService.getNewsDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 조회수 증가
     * 
     * POST /api/news/{id}/view
     * 
     * @param id 뉴스 ID
     * @return 성공 메시지
     */
    @Operation(summary = "조회수 증가", description = "뉴스 조회수를 1 증가시킵니다.")
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> increaseViewCount(@PathVariable Long id) {
        log.info("조회수 증가 - 뉴스 ID: {}", id);

        newsService.increaseViewCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 증가
     * 
     * POST /api/news/{id}/heart
     * 
     * @param id 뉴스 ID
     * @return 성공 메시지
     */
    @Operation(summary = "좋아요 증가", description = "뉴스 좋아요를 1 증가시킵니다.")
    @PostMapping("/{id}/heart")
    public ResponseEntity<Void> increaseHeartCount(@PathVariable Long id) {
        log.info("좋아요 증가 - 뉴스 ID: {}", id);

        newsService.increaseHeartCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 취소
     * 
     * DELETE /api/news/{id}/heart
     * 
     * @param id 뉴스 ID
     * @return 성공 메시지
     */
    @Operation(summary = "좋아요 취소", description = "뉴스 좋아요를 1 감소시킵니다.")
    @DeleteMapping("/{id}/heart")
    public ResponseEntity<Void> decreaseHeartCount(@PathVariable Long id) {
        log.info("좋아요 취소 - 뉴스 ID: {}", id);

        newsService.decreaseHeartCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 신고 증가
     * 
     * POST /api/news/{id}/report
     * 
     * @param id 뉴스 ID
     * @return 성공 메시지
     */
    @Operation(summary = "신고 증가", description = "뉴스 신고 수를 1 증가시킵니다.")
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> increaseReportCount(@PathVariable Long id) {
        log.info("신고 증가 - 뉴스 ID: {}", id);

        newsService.increaseReportCount(id);
        return ResponseEntity.ok().build();
    }
}
