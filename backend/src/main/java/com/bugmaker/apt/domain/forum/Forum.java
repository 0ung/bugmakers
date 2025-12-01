package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("토론 마스터 테이블")
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("토론 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;

    @Column(nullable = false, length = 200)
    @Comment("토론명")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("토론 내용")
    private String content;

    @Column(nullable = false)
    @Comment("조회수")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Comment("좋아요 누적수")
    @Builder.Default
    private Long heartCount = 0L;

    @Column(nullable = false)
    @Comment("신고 누적수")
    @Builder.Default
    private Long reportCount = 0L;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime lastModifiedDate;

    // 비즈니스 메서드
    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseHeartCount() {
        this.heartCount++;
    }

    public void decreaseHeartCount() {
        if (this.heartCount > 0) {
            this.heartCount--;
        }
    }

    public void increaseReportCount() {
        this.reportCount++;
    }
}
