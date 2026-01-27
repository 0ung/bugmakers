package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Shared;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * SharedRepository - "내가 무엇을 공유했는가"
 * 마이페이지(USER): 내가 공유한 것들
 */
public interface SharedRepository extends JpaRepository<Shared, Long> {

    /* 기본 CRUD */
    Optional<Shared> findByMember_IdAndNews_Id(Long memberId, Long newsId);
    Optional<Shared> findByMember_IdAndForum_Id(Long memberId, Long forumId);

    boolean existsByMember_IdAndNews_Id(Long memberId, Long newsId);
    boolean existsByMember_IdAndForum_Id(Long memberId, Long forumId);

    void deleteByMember_IdAndNews_Id(Long memberId, Long newsId);
    void deleteByMember_IdAndForum_Id(Long memberId, Long forumId);


    /* 마이페이지(USER) - 내가 공유한 개수 */
    @Query("""
        SELECT COUNT(s)
        FROM Shared s
        WHERE s.member.id = :memberId
          AND s.news IS NOT NULL
    """)
    Long countMySharedNews(@Param("memberId") Long memberId);

    @Query("""
        SELECT COUNT(s)
        FROM Shared s
        WHERE s.member.id = :memberId
          AND s.forum IS NOT NULL
    """)
    Long countMySharedForums(@Param("memberId") Long memberId);

    @Query("""
        SELECT COUNT(s)
        FROM Shared s
        WHERE s.member.id = :memberId
    """)
    Long countAllMyShares(@Param("memberId") Long memberId);


    /* 마이페이지(USER) - 내가 공유한 목록 */
    @Query("""
        SELECT s.news
        FROM Shared s
        WHERE s.member.id = :memberId
          AND s.news IS NOT NULL
        ORDER BY s.createdDate DESC
    """)
    Page<News> findMySharedNews(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    @Query("""
        SELECT s.forum
        FROM Shared s
        WHERE s.member.id = :memberId
          AND s.forum IS NOT NULL
        ORDER BY s.createdDate DESC
    """)
    Page<Forum> findMySharedForums(
            @Param("memberId") Long memberId,
            Pageable pageable
    );
}
