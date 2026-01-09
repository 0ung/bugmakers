package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 뉴스 Repository
 * RSS 크롤링으로 수집된 뉴스 데이터를 관리
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    /**
     * 모든 뉴스 목록 조회 (페이징)
     * 최신순 정렬은 Controller에서 Pageable로 처리
     */
    Page<News> findAll(Pageable pageable);
    
    /**
     * 제목으로 뉴스 검색 (중복 체크용)
     * Python 크롤러에서 중복 등록 방지를 위해 사용
     */
    Optional<News> findByTitle(String title);
    
    /**
     * 제목 존재 여부 확인 (중복 체크)
     */
    boolean existsByTitle(String title);
    
    /**
     * 출처(URL)로 뉴스 검색 (중복 체크용)
     */
    Optional<News> findByReference(String reference);
    
    /**
     * 출처 존재 여부 확인
     */
    boolean existsByReference(String reference);
}
