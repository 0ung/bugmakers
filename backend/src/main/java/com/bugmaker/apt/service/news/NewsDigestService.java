package com.bugmaker.apt.service.news;

import com.bugmaker.apt.domain.news.*;
import com.bugmaker.apt.enums.news.NewsCategory;
import com.bugmaker.apt.repository.news.NewsDigestRepository;
import com.bugmaker.apt.repository.news.NewsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MCP 서버용 News Digest 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsDigestService {

    private final NewsRepository newsRepository;
    private final NewsDigestRepository newsDigestRepository;
    private final ObjectMapper objectMapper;

    /**
     * 1. 요약 대기 중인 뉴스 목록 조회
     * - 어제 날짜의 뉴스를 카테고리별로 그룹화
     * - 이미 다이제스트가 생성된 카테고리는 제외
     */
    public List<PendingNewsResponse> getPendingNews() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        List<PendingNewsResponse> result = new ArrayList<>();
        
        // 모든 카테고리에 대해 확인
        for (NewsCategory category : NewsCategory.values()) {
            // 이미 다이제스트가 있으면 스킵
            if (newsDigestRepository.existsByDigestDateAndCategory(yesterday, category)) {
                continue;
            }
            
            // 해당 날짜 + 카테고리의 뉴스 조회
            List<News> newsList = newsRepository.findByCreatedDateAndCategory(yesterday, category);
            
            if (newsList.isEmpty()) {
                continue;
            }
            
            // DTO 변환
            List<PendingNewsResponse.NewsItem> newsItems = newsList.stream()
                    .map(news -> PendingNewsResponse.NewsItem.builder()
                            .id(news.getId())
                            .title(news.getTitle())
                            .content(news.getContent())
                            .reference(news.getReference())
                            .build())
                    .collect(Collectors.toList());
            
            result.add(PendingNewsResponse.builder()
                    .digestDate(yesterday.toString())
                    .category(category)
                    .newsCount(newsItems.size())
                    .newsList(newsItems)
                    .build());
        }
        
        log.info("[MCP] 요약 대기 중인 그룹: {}개", result.size());
        return result;
    }

    /**
     * 2. AI 요약 저장
     */
    @Transactional
    public DigestResponse createDigest(DigestCreateRequest request) {
        LocalDate digestDate = LocalDate.parse(request.getDigestDate());
        
        // 중복 체크
        if (newsDigestRepository.existsByDigestDateAndCategory(digestDate, request.getCategory())) {
            throw new IllegalArgumentException("이미 해당 날짜/카테고리의 다이제스트가 존재합니다");
        }
        
        // 뉴스 ID 목록을 JSON으로 변환
        String newsIdsJson;
        try {
            newsIdsJson = objectMapper.writeValueAsString(request.getNewsIds());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("뉴스 ID 목록 변환 실패", e);
        }
        
        // NewsDigest 생성
        NewsDigest digest = NewsDigest.create(
                digestDate,
                request.getCategory(),
                request.getSummary(),
                newsIdsJson,
                request.getNewsIds().size()
        );
        
        NewsDigest saved = newsDigestRepository.save(digest);
        log.info("[MCP] 다이제스트 생성: {} / {} / {} 건", digestDate, request.getCategory(), request.getNewsIds().size());
        
        return convertToResponse(saved);
    }

    /**
     * 3. 특정 날짜/카테고리의 다이제스트 조회
     */
    public DigestResponse getDigest(String date, NewsCategory category) {
        LocalDate digestDate = LocalDate.parse(date);
        
        NewsDigest digest = newsDigestRepository.findByDigestDateAndCategory(digestDate, category)
                .orElseThrow(() -> new IllegalArgumentException("다이제스트를 찾을 수 없습니다"));
        
        return convertToResponse(digest);
    }

    /**
     * 4. 특정 날짜의 모든 다이제스트 조회
     */
    public List<DigestResponse> getDigestsByDate(String date) {
        LocalDate digestDate = LocalDate.parse(date);
        
        return Arrays.stream(NewsCategory.values())
                .map(category -> newsDigestRepository.findByDigestDateAndCategory(digestDate, category))
                .filter(opt -> opt.isPresent())
                .map(opt -> convertToResponse(opt.get()))
                .collect(Collectors.toList());
    }

    // === Private Methods ===
    
    private DigestResponse convertToResponse(NewsDigest digest) {
        List<Long> newsIds;
        try {
            newsIds = objectMapper.readValue(digest.getNewsIds(), new TypeReference<List<Long>>() {});
        } catch (JsonProcessingException e) {
            newsIds = List.of();
        }
        
        return DigestResponse.builder()
                .id(digest.getId())
                .digestDate(digest.getDigestDate().toString())
                .category(digest.getCategory())
                .summary(digest.getSummary())
                .newsIds(newsIds)
                .newsCount(digest.getNewsCount())
                .createdAt(digest.getCreatedDate())
                .build();
    }
}
