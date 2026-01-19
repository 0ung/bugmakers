package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.news.News;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("좋아요 마스터 테이블")
public class Liked {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("좋아요 ID")
    private Long id;

    @Column(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Long memberId;

    @Column(name = "news_id")
    @Comment("뉴스 ID")
    private Long newsId;

    @Column(name = "forum_id")
    @Comment("토론 ID")
    private Long forumId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;

    public static Liked forNews(Long memberId, Long newsId) {
        return Liked.builder()
                .memberId(memberId)
                .newsId(newsId)
                .forumId(null)
                .build();
    }

    public static Liked forForum(Long memberId, Long forumId) {
        return Liked.builder()
                .memberId(memberId)
                .forumId(forumId)
                .newsId(null)
                .build();
    }

    public boolean isNewsLike() {
        return newsId != null;
    }

    public boolean isForumLike() {
        return forumId != null;
    }
}
