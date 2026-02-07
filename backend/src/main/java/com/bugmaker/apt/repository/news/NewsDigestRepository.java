package com.bugmaker.apt.repository.news;

import com.bugmaker.apt.domain.news.NewsDigest;
import com.bugmaker.apt.enums.news.NewsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface NewsDigestRepository extends JpaRepository<NewsDigest, Long> {
    
    /**
     * 특정 날짜와 카테고리의 다이제스트 조회
     */
    Optional<NewsDigest> findByDigestDateAndCategory(LocalDate digestDate, NewsCategory category);
    
    /**
     * 특정 날짜와 카테고리의 다이제스트 존재 여부
     */
    boolean existsByDigestDateAndCategory(LocalDate digestDate, NewsCategory category);
}
