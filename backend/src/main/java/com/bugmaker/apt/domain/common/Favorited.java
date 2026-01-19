package com.bugmaker.apt.domain.common;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favorited",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_favorited_member_news",
                        columnNames = {"member_id", "news_id"}
                ),
                @UniqueConstraint(
                        name = "uk_favorited_member_forum",
                        columnNames = {"member_id", "forum_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("즐겨찾기 마스터 테이블")
public class Favorited {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("즐겨찾기 ID")
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

    /** 뉴스 즐겨찾기 생성 */
    public static Favorited forNews(Long memberId, Long newsId) {
        return Favorited.builder()
                .memberId(memberId)
                .newsId(newsId)
                .forumId(null)
                .build();
    }

    /** 토론 즐겨찾기 생성 */
    public static Favorited forForum(Long memberId, Long forumId) {
        return Favorited.builder()
                .memberId(memberId)
                .forumId(forumId)
                .newsId(null)
                .build();
    }

    /** 뉴스 즐겨찾기 여부 확인 */
    public boolean isNewsFavorite() {
        return newsId != null;
    }

    /** 토론 즐겨찾기 여부 확인 */
    public boolean isForumFavorite() {
        return forumId != null;
    }
}
