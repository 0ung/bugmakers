package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.enums.menu.MenuLevel;
import com.bugmaker.apt.enums.member.Status;
import com.bugmaker.apt.domain.common.Commented;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CommentedRepository extends JpaRepository<Commented, Long> {

    /* 기본 CRUD */

    // 포럼 전체 댓글
    List<Commented> findByForum_Id(Long forumId);

    // 댓글 / 대댓글 공용
    List<Commented> findByForum_IdAndSeq(Long forumId, MenuLevel seq);

    // 작성자 기준
    List<Commented> findByMember_Id(Long memberId);
    List<Commented> findByMember_IdAndForum_Id(Long memberId, Long forumId);

    // 단건 / 검증
    Optional<Commented> findByIdAndForum_Id(Long commentedId, Long forumId);
    boolean existsByIdAndForum_Id(Long commentedId, Long forumId);

    // 집계
    long countByForum_Id(Long forumId);
    long countByMember_Id(Long memberId);
    void deleteByIdAndMember_Id(Long commentedId, Long memberId);


    /*  마이페이지(USER) */
    // 내가 작성한 댓글 목록 (페이징)
    Page<Commented> findByMember_Id(Long memberId, Pageable pageable);

    // 내 포럼 글에 달린 댓글 목록 (페이징)
    @Query("""
        SELECT c 
        FROM Commented c
        WHERE c.forum.member.id = :memberId 
          AND c.forum.status = com.bugmaker.apt.enums.member.Status.ACTIVE
        ORDER BY c.createdDate DESC
    """)
    Page<Commented> findCommentsOnMyForums(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    // 내 댓글에 받은 좋아요 개수
    @Query("""
        SELECT COUNT(l) 
        FROM Liked l
        WHERE l.commented.member.id = :memberId 
          AND l.commented IS NOT NULL
    """)
    Long countLikesOnMyComments(@Param("memberId") Long memberId);
}
