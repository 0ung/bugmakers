package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "liked",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_liked_member_news",
                        columnNames = {"member_id", "news_id"}
                ),
                @UniqueConstraint(
                        name = "uk_liked_member_forum",
                        columnNames = {"member_id", "forum_id"}
                ),
                @UniqueConstraint(
                        name = "uk_liked_member_comment",
                        columnNames = {"member_id", "comment_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("좋아요 테이블")
public class Liked extends BaseEntity {

    /* 좋아요 주체 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    @Comment("뉴스 ID")
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    @Comment("토론 ID")
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @Comment("댓글 ID")
    private Commented commented;

    /* 생성 팩토리 */
    public static Liked forNews(Member member, News news) {
        return Liked.builder()
                .member(member)
                .news(news)
                .build();
    }

    public static Liked forForum(Member member, Forum forum) {
        return Liked.builder()
                .member(member)
                .forum(forum)
                .build();
    }

    public static Liked forComment(Member member, Commented commented) {
        return Liked.builder()
                .member(member)
                .commented(commented)
                .build();
    }

    /* 타입 판별 (좋아요 여부 확인) */
    public boolean isNewsLike() { return news != null; }

    public boolean isForumLike() { return forum != null; }

    public boolean isCommentLike() { return commented != null; }
}
