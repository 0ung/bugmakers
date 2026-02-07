package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Favorited;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FavoritedRepository extends JpaRepository<Favorited, Long> {

    /* 기본 CRUD */
    // 이미 즐겨찾기 눌렀는지 확인
    Optional<Favorited> findByMember_IdAndNews_Id(Long memberId, Long newsId);
    Optional<Favorited> findByMember_IdAndForum_Id(Long memberId, Long forumId);

    // UI에서 ⭐ 표시할 때 사용
    boolean existsByMember_IdAndNews_Id(Long memberId, Long newsId);
    boolean existsByMember_IdAndForum_Id(Long memberId, Long forumId);

    // 즐겨찾기 취소 (물리 삭제)
    void deleteByMember_IdAndNews_Id(Long memberId, Long newsId);
    void deleteByMember_IdAndForum_Id(Long memberId, Long forumId);


    /* 마이페이지(USER) - 내가 즐겨찾기 한 개수 */
    // 1. 내가(주체) news 게시물에(목적) 즐겨찾기 한 count
    @Query("""
    SELECT COUNT(l)
    FROM Favorited l
    WHERE l.member.id = :memberId
      AND l.news IS NOT NULL
    """)
    Long countMyFavoritedNews(@Param("memberId") Long memberId);

    // 2. 내가(주체) forum 게시물에(목적) 즐겨찾기 한 count
    @Query("""
    SELECT COUNT(l)
    FROM Favorited l
    WHERE l.member.id = :memberId
      AND l.forum IS NOT NULL
    """)
    Long countMyFavoritedForums(@Param("memberId") Long memberId);

    // 3. 내가(주체) 모든 게시물(news, forum)에(목적) 즐겨찾기 한 count
    @Query("""
    SELECT COUNT(l)
    FROM Favorited l
    WHERE l.member.id = :memberId
    """)
    Long countAllMyFavorites(@Param("memberId") Long memberId);


    /* 마이페이지(USER) - 내가 즐겨찾기 한 목록 조회 */
    // 1. 내가 즐겨찾기 한 뉴스 목록
    @Query("""
    SELECT n
    FROM Favorited f
    JOIN f.news n
    WHERE f.member.id = :memberId
    ORDER BY f.createdDate DESC
    """)
    Page<News> findMyFavoritedNews(Long memberId, Pageable pageable);

    // 2. 내가 즐겨찾기 한 포럼 목록
    @Query("""
    SELECT f
    FROM Favorited l
    JOIN l.forum f
    WHERE l.member.id = :memberId
    ORDER BY l.createdDate DESC
    """)
    Page<Forum> findMyFavoritedForums(Long memberId, Pageable pageable);

}
