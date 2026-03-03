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
        name = "shared",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shared_member_news",
                        columnNames = {"member_id", "news_id"}
                ),
                @UniqueConstraint(
                        name = "uk_shared_member_forum",
                        columnNames = {"member_id", "forum_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("공유 테이블")
public class Shared extends BaseEntity {

    /* 공유 주체 */
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

    /* 생성 팩토리 */
    public static Shared forNews(Member member, News news) {
        return Shared.builder()
                .member(member)
                .news(news)
                .build();
    }

    public static Shared forForum(Member member, Forum forum) {
        return Shared.builder()
                .member(member)
                .forum(forum)
                .build();
    }

    /* 타입 판별 (공유 여부 확인) */
    public boolean isNewsShare() {
        return news != null;
    }

    public boolean isForumShare() {
        return forum != null;
    }
}
