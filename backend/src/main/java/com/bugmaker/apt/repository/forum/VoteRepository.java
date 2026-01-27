package com.bugmaker.apt.repository.forum;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    /* 기본 CRUD */
    Optional<Vote> findByForum_Id(Long forumId);
    boolean existsByForum_Id(Long forumId);


    /* 마이페이지(USER) - 참여중인 토론(투표) */
    // 내가 투표한 포럼 목록
    @Query("""
        SELECT DISTINCT vd.vote.forum
        FROM VoteDetail vd
        WHERE vd.member.id = :memberId
        ORDER BY vd.createdDate DESC
    """)
    Page<Forum> findForumsIVoted(
            @Param("memberId") Long memberId,
            Pageable pageable
    );

    // 내가 투표한 포럼 개수
    @Query("""
        SELECT COUNT(DISTINCT vd.vote.forum.id)
        FROM VoteDetail vd
        WHERE vd.member.id = :memberId
    """)
    Long countForumsIVoted(@Param("memberId") Long memberId);

    // 특정 투표에 내가 참여했는지 확인
    @Query("""
        SELECT COUNT(vd) > 0
        FROM VoteDetail vd
        WHERE vd.vote.id = :voteId 
          AND vd.member.id = :memberId
    """)
    boolean existsMyVote(@Param("voteId") Long voteId, @Param("memberId") Long memberId);
}
