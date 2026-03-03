package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "comment_like")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CommentLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static CommentLike create(Comment comment, Member member) {
        CommentLike like = new CommentLike();
        like.comment = comment;
        like.member = member;
        return like;
    }
}
