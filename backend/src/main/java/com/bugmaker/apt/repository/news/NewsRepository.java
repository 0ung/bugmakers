package com.bugmaker.apt.repository.news;

import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.enums.news.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    /** 제목 존재 여부 확인 (중복 체크) */
    boolean existsByTitle(String title);
    
    /** 출처(URL)로 뉴스 검색 (중복 체크용) */
    Optional<News> findByReference(String reference);
    
    /** 출처 존재 여부 확인 */
    boolean existsByReference(String reference);

    /** 카테고리별 뉴스 조회 */
    Page<News> findByCategory(NewsCategory category, Pageable pageable);

    /** 카테고리 + 검색어로 뉴스 조회 */
    @Query("SELECT n FROM News n " +
            "WHERE n.category = :category " +
            "AND LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<News> findByCategoryAndKeyword(@Param("category") NewsCategory category,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    /** 검색어로 뉴스 조회 */
    @Query("SELECT n FROM News n " +
            "WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<News> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * DB에 실제로 저장된 뉴스의 카테고리 목록 조회 (중복 제거)
     * NewsCategory Enum 반환
     */
    @Query("SELECT DISTINCT n.category FROM News n WHERE n.category IS NOT NULL ORDER BY n.category")
    List<NewsCategory> findDistinctCategories();



//    // 삭제된 것도 보려면 native query 사용
//    @Query(value = "SELECT * FROM news WHERE id = :id", nativeQuery = true)
//    News findByIdIncludingDeleted(@Param("id") Long id);
}
