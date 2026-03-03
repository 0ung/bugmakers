package com.bugmaker.apt.repository.forum;

import com.bugmaker.apt.domain.forum.VoteDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteDetailRepository extends JpaRepository<VoteDetail, Long> {

    long countByVoteId(Long voteId);

    // 특정 포럼에서 해당 회원이 투표한 기록 조회 (중복 투표 방어를 위해 List 반환)
    @Query("SELECT vd FROM VoteDetail vd WHERE vd.vote.forum.id = :forumId AND vd.member.id = :memberId")
    List<VoteDetail> findAllByForumIdAndMemberId(@Param("forumId") Long forumId, @Param("memberId") Long memberId);

    // 특정 포럼의 전체 투표 수
    @Query("SELECT COUNT(vd) FROM VoteDetail vd WHERE vd.vote.forum.id = :forumId")
    long countByForumId(@Param("forumId") Long forumId);

    // 여러 포럼의 투표 항목별 득표수 일괄 조회 [forumId, voteName, count]
    @Query("SELECT vd.vote.forum.id, vd.vote.name, COUNT(vd) FROM VoteDetail vd WHERE vd.vote.forum.id IN :forumIds GROUP BY vd.vote.forum.id, vd.vote.id, vd.vote.name")
    List<Object[]> findVoteCountsByForumIds(@Param("forumIds") List<Long> forumIds);
}
