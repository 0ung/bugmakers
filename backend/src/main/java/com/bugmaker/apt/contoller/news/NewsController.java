package com.bugmaker.apt.contoller.news;

import com.bugmaker.apt.domain.news.NewsCreateRequest;
import com.bugmaker.apt.domain.news.NewsDetailResponse;
import com.bugmaker.apt.domain.news.NewsResponse;
import com.bugmaker.apt.service.news.NewsService;
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
    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
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
    @GetMapping
    public ResponseEntity<Page<NewsResponse>> getNewsList(
            @RequestParam(defaultValue = "0") int page,
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
    @GetMapping("/{id}")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(@PathVariable Long id) {
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
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> increaseReportCount(@PathVariable Long id) {
        log.info("신고 증가 - 뉴스 ID: {}", id);

        newsService.increaseReportCount(id);
        return ResponseEntity.ok().build();
    }
}
