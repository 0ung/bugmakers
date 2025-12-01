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
@Table(name = "favorited")
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;
}
