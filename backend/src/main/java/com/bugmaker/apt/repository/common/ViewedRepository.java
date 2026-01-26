package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Viewed;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ViewedRepository extends JpaRepository<Viewed, Long> {

    /* 기본 CRUD */
    List<Viewed> findByMember_Id(Long memberId);
    List<Viewed> findByMember_IdAndNews_Id(Long memberId, Long newsId);
    List<Viewed> findByMember_IdAndForum_Id(Long memberId, Long forumId);

    /* 집계 */
    long countByMember_Id(Long memberId);
    long countByMember_IdAndNews_IsNotNull(Long memberId);
    long countByMember_IdAndForum_IsNotNull(Long memberId);

    /* 마이페이지(USER) - 자주 보는 컨텐츠 */
    // 1. 자주 보는 뉴스 (조회수 순위)
    @Query("""
        SELECT v.news 
        FROM Viewed v
        WHERE v.member.id = :memberId 
          AND v.news IS NOT NULL
        GROUP BY v.news
        ORDER BY COUNT(v) DESC
    """)
    Page<News> findFrequentNews(@Param("memberId") Long memberId, Pageable pageable);

    // 2. 자주 보는 포럼 (조회수 순위)
    @Query("""
        SELECT v.forum 
        FROM Viewed v
        WHERE v.member.id = :memberId 
          AND v.forum IS NOT NULL
        GROUP BY v.forum
        ORDER BY COUNT(v) DESC
    """)
    Page<Forum> findFrequentForums(@Param("memberId") Long memberId, Pageable pageable);
}