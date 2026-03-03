package com.bugmaker.apt.repository.forum;

import com.bugmaker.apt.domain.forum.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);

    @Query("SELECT cl.comment.id FROM CommentLike cl WHERE cl.comment.id IN :commentIds AND cl.member.id = :memberId")
    List<Long> findLikedCommentIdsByMemberId(@Param("commentIds") List<Long> commentIds, @Param("memberId") Long memberId);
}
