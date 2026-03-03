package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 쿼리 없이 FK 값만 읽기 위한 파생 컬럼
    @Column(name = "parent_id", insertable = false, updatable = false)
    private Long parentId;

    private long likeCount;

    public static Comment create(String content, Forum forum, Member member, Comment parent) {
        Comment comment = new Comment();
        comment.content = content;
        comment.forum = forum;
        comment.member = member;
        comment.parent = parent;
        comment.likeCount = 0;
        return comment;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) this.likeCount--;
    }
}
