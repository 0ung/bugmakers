package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Commented;
import com.bugmaker.apt.domain.common.Liked;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.dto.mypage.UserMyPageListResponse;
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
    // 이미 좋아요 눌렀는지 확인
    Optional<Liked> findByMember_IdAndNews_Id(Long memberId, Long newsId);
    Optional<Liked> findByMember_IdAndForum_Id(Long memberId, Long forumId);
    Optional<Liked> findByMember_IdAndCommented_Id(Long memberId, Long commentId);

    // UI에서 ❤️ 표시할 때 사용
    boolean existsByMember_IdAndNews_Id(Long memberId, Long newsId);
    boolean existsByMember_IdAndForum_Id(Long memberId, Long forumId);
    boolean existsByMember_IdAndCommented_Id(Long memberId, Long commentId);

    // 좋아요 취소 (물리 삭제)
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
    Long countMyLikedNews(@Param("memberId") Long memberId);

    // 2. 내가(주체) forum 게시물에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.forum IS NOT NULL
    """)
    Long countMyLikedForums(@Param("memberId") Long memberId);

    // 3. 내가(주체) 댓글에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
      AND l.commented IS NOT NULL
    """)
    Long countMyLikedComments(@Param("memberId") Long memberId);

    // 4. 내가(주체) 모든 게시물(news, forum, comment)에(목적) 좋아요를 누른 count
    @Query("""
    SELECT COUNT(l)
    FROM Liked l
    WHERE l.member.id = :memberId
    """)
    Long countAllMyLikes(@Param("memberId") Long memberId);


    /* 마이페이지(USER) - 내가 좋아요 한 목록 조회 */
    // 1. 내가 좋아요한 뉴스 목록*
    @Query("""
    SELECT n
    FROM Liked l
    JOIN l.news n
    WHERE l.member.id = :memberId
    ORDER BY l.createdDate DESC
    """)
    Page<News> findMyLikedNews(Long memberId, Pageable pageable);

    // 2. 내가 좋아요한 포럼 목록
    @Query("""
    SELECT f
    FROM Liked l
    JOIN l.forum f
    WHERE l.member.id = :memberId
    ORDER BY l.createdDate DESC
    """)
    Page<Forum> findMyLikedForums(Long memberId, Pageable pageable);

    // 댓글 수 포함
//    @Query("""
//    select f, count(c)
//    from Forum f
//    left join Commented c on c.forum.id = f.id
//    where f.id in (
//        select l.forum.id
//        from Liked l
//        where l.member.id = :memberId
//    )
//    group by f
//    """)
//    Page<Object[]> findMyLikedForums(
//            @Param("memberId") Long memberId,
//            Pageable pageable
//    );

    // 3. 내가 좋아요한 댓글 목록
    @Query("""
    SELECT c
    FROM Liked l
    JOIN l.commented c
    WHERE l.member.id = :memberId
    ORDER BY l.createdDate DESC
    """)
    Page<Commented> findMyLikedComments(Long memberId, Pageable pageable);

    // 4. 내가(주체) 모든 게시물(news, forum, comment)에(목적) 좋아요를 누른 count
    @Query("""
    SELECT new com.bugmaker.apt.dto.mypage.UserMyPageListResponse(
        COALESCE(n.id, f.id, c.id),
        CASE 
            WHEN n IS NOT NULL THEN 'news'
            WHEN f IS NOT NULL THEN 'forum'
            ELSE 'comment'
        END,
        COALESCE(n.title, f.title, '댓글'),
        COALESCE(n.content, f.content, c.content),
        COALESCE(n.reference, f.member.nickname, c.member.nickname),
        COALESCE(n.createdDate, f.createdDate, c.createdDate),
        COALESCE(n.viewCount, f.viewCount, 0L),
        COALESCE(n.likeCount, f.likeCount, c.likeCount),
        /* 🔥 forum commentCount 계산 */
        CASE 
            WHEN f IS NOT NULL THEN (
                SELECT COUNT(fc) FROM Commented fc WHERE fc.forum.id = f.id
            )
            ELSE 0L
        END
    )
    FROM Liked l
    LEFT JOIN l.news n
    LEFT JOIN l.forum f
    LEFT JOIN l.commented c
    WHERE l.member.id = :memberId
    ORDER BY l.createdDate DESC
    """)
    Page<UserMyPageListResponse> findAllMyLikedActivities(Long memberId, Pageable pageable);

}
