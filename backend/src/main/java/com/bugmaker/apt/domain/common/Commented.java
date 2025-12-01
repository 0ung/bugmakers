package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.constants.CommentSeq;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.news.News;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "commented")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("댓글 마스터 테이블")
public class Commented {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    @Comment("뉴스 ID")
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    @Comment("토론 ID")
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    @Comment("댓글 순서 (1: 댓글, 2: 대댓글)")
    private CommentSeq seq;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime lastModifiedDate;
}
