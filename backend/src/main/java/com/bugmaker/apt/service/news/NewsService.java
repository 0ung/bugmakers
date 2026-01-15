package com.bugmaker.apt.service.news;

import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.domain.news.NewsCreateRequest;
import com.bugmaker.apt.domain.news.NewsDetailResponse;
import com.bugmaker.apt.domain.news.NewsResponse;
import com.bugmaker.apt.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 뉴스 서비스
 * RSS 크롤링 데이터 관리 및 뉴스 CRUD
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;

    /**
     * 뉴스 등록 (Python 크롤러용)
     * 중복 체크: 제목과 출처 모두 확인
     * Idempotent: 중복된 뉴스는 기존 데이터 반환 (예외 없음)
     * 
     * @param request 뉴스 생성 요청
     * @return 생성된 또는 기존 뉴스 응답
     * @throws IllegalArgumentException 필수 값이 없는 경우
     */
    @Transactional
    public NewsResponse createNews(NewsCreateRequest request) {
        // 1. Validation: 필수 값 체크
        validateNewsRequest(request);
        
        log.info("뉴스 등록 시도 - 제목: {}", request.title());

        // 2. 중복 체크: 제목으로 기존 뉴스 확인
        Optional<News> existingByTitle = newsRepository.findByTitle(request.title());
        if (existingByTitle.isPresent()) {
            News existing = existingByTitle.get();
            log.warn("⚠️  중복 뉴스 (제목) - ID: {}, 제목: {}", existing.getId(), existing.getTitle());
            return NewsResponse.from(existing);
        }

        // 3. 중복 체크: 출처로 기존 뉴스 확인
        Optional<News> existingByReference = newsRepository.findByReference(request.reference());
        if (existingByReference.isPresent()) {
            News existing = existingByReference.get();
            log.warn("⚠️  중복 뉴스 (출처) - ID: {}, 출처: {}", existing.getId(), existing.getReference());
            return NewsResponse.from(existing);
        }

        // 4. 새 뉴스 저장
        News news = request.toEntity();
        News savedNews = newsRepository.save(news);

        log.info("✅ 뉴스 등록 성공 - ID: {}, 제목: {}", savedNews.getId(), savedNews.getTitle());
        return NewsResponse.from(savedNews);
    }

    /**
     * 뉴스 등록 요청 검증
     * 
     * @param request 뉴스 생성 요청
     * @throws IllegalArgumentException 필수 값이 없는 경우
     */
    private void validateNewsRequest(NewsCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("뉴스 등록 요청이 null입니다.");
        }
        
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 제목은 필수입니다.");
        }
        
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 내용은 필수입니다.");
        }
        
        if (request.reference() == null || request.reference().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 출처는 필수입니다.");
        }
    }

    /**
     * 뉴스 목록 조회 (페이징)
     * 최신순 정렬
     * 
     * @param pageable 페이징 정보
     * @return 뉴스 목록
     */
    public Page<NewsResponse> getNewsList(Pageable pageable) {
        log.info("뉴스 목록 조회 - 페이지: {}, 사이즈: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<News> newsPage = newsRepository.findAll(pageable);
        
        log.info("뉴스 목록 조회 완료 - 총 {}건", newsPage.getTotalElements());
        return newsPage.map(NewsResponse::from);
    }

    /**
     * 뉴스 상세 조회
     * 조회수 증가는 별도 메서드로 분리
     * 
     * @param newsId 뉴스 ID
     * @return 뉴스 상세 정보
     * @throws IllegalArgumentException 뉴스를 찾을 수 없는 경우
     */
    public NewsDetailResponse getNewsDetail(Long newsId) {
        log.info("뉴스 상세 조회 - ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));

        return NewsDetailResponse.from(news);
    }

    /**
     * 조회수 증가
     * 
     * @param newsId 뉴스 ID
     */
    @Transactional
    public void increaseViewCount(Long newsId) {
        log.info("조회수 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));

        news.increaseViewCount();
        // JPA 더티 체킹으로 자동 업데이트
    }

    /**
     * 좋아요 증가
     * 
     * @param newsId 뉴스 ID
     */
    @Transactional
    public void increaseHeartCount(Long newsId) {
        log.info("좋아요 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));

        news.increaseHeartCount();
    }

    /**
     * 좋아요 취소
     * 
     * @param newsId 뉴스 ID
     */
    @Transactional
    public void decreaseHeartCount(Long newsId) {
        log.info("좋아요 취소 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));

        news.decreaseHeartCount();
    }

    /**
     * 신고 증가
     * 
     * @param newsId 뉴스 ID
     */
    @Transactional
    public void increaseReportCount(Long newsId) {
        log.info("신고 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("뉴스를 찾을 수 없습니다. ID: " + newsId));

        news.increaseReportCount();
    }
}
