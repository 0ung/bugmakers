package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Commented;
import com.bugmaker.apt.domain.common.Liked;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.news.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * LikedRepository - "내가 무엇에 좋아요 했는가" (행위 / 주체 기준)
 * 마이페이지(USER): 내가 좋아요한 것들
 */
public interface LikedRepository extends JpaRepository<Liked, Long> {

    /* 기본 CRUD */
    Optional<Liked> findByMember_IdAndNews_Id(Long memberId, Long newsId);
    Optional<Liked> findByMember_IdAndForum_Id(Long memberId, Long forumId);
    Optional<Liked> findByMember_IdAndCommented_Id(Long memberId, Long commentId);

    boolean existsByMember_IdAndNews_Id(Long memberId, Long newsId);
    boolean existsByMember_IdAndForum_Id(Long memberId, Long forumId);
    boolean existsByMember_IdAndCommented_Id(Long memberId, Long commentId);

    void deleteByMember_IdAndNews_Id(Long memberId, Long newsId);
    void deleteByMember_IdAndForum_Id(Long memberId, Long forumId);
    void deleteByMember_IdAndCommented_Id(Long memberId, Long commentId);


    /* 마이페이지(USER) - 내가 좋아요한 개수 */
    // 1. 내가(주체) news 게시물에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.news IS NOT NULL
    """)
    Long countMyLikedNews(@Param("reportedId") Long memberId);

    // 2. 내가(주체) forum 게시물에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.forum IS NOT NULL
    """)
    Long countMyLikedForums(@Param("reportedId") Long memberId);

    // 3. 내가(주체) 댓글에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.commented IS NOT NULL
    """)
    Long countMyLikedComments(@Param("reportedId") Long memberId);

    // 4. 내가(주체) 모든 게시물(news, forum, comment)에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
    """)
    Long countAllMyLikes(@Param("reportedId") Long memberId);


    /* 마이페이지(USER) - 내가 좋아요 한 목록 조회 */
    // 1. 내가 좋아요한 뉴스 목록*
    @Query("""
    SELECT l.news
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.news IS NOT NULL
    ORDER BY l.createdDate DESC
    """)
    Page<News> findMyLikedNews(
            @Param("reportedId") Long memberId, Pageable pageable);

    // 2. 내가 좋아요한 포럼 목록
    @Query("""
    SELECT l.forum
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.forum IS NOT NULL
    ORDER BY l.createdDate DESC
    """)
    Page<Forum> findMyLikedForums(
            @Param("reportedId") Long memberId,
            Pageable pageable
    );

    // 3. 내가 좋아요한 댓글 목록
    @Query("""
    SELECT l.commented
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.commented IS NOT NULL
    ORDER BY l.createdDate DESC
    """)
    Page<Commented> findMyLikedComments(
            @Param("reportedId") Long memberId,
            Pageable pageable
    );

}
