package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.enums.common.MenuLevel;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static io.jsonwebtoken.lang.Assert.state;

@Entity
@Table(name = "commented")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("댓글 테이블")
public class Commented extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("작성자") //댓글 or 대댓글 작성자
    private Member member;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    @Comment("토론 ID")
    private Forum forum;

/*
수정 : EnumType.ORDINAL -> EnumType.STRING 변경
원인 : Java Enum CommentSeq 에서 정의된 숫자는 1,2 인데, ORDINAL의 경우 무조건 0부터 시작하며, 사용자 정의된 value는 무시함.
수정 근거 : 실무에서는 ORDINAL을 거의 사용하지 않음
          ORDINAL : enum 순서 변경 불가 (0부터) / 중간 값 추가 불가 / 의미추측 어려움
          STRING :  enum 순서 변경 영향 없음 / enum 중간에 값 추가 가능 / DB 가독성 최고 / 문자열 저장 (영향력 크지 않은 용량 증가)
*/
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("댓글 순서 (1: 댓글, 2: 대댓글)")
    private MenuLevel seq;

    @Column(nullable = false)
    @Comment("좋아요 누적수")
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Comment("신고 누적수")
    @Builder.Default
    private Long reportCount = 0L;


     /* 팩토리 메서드 */
    // 토론 댓글 작성 (1 depth)
    public static Commented writeForumComment(Member member, Forum forum) {
        return Commented.builder()
                .member(member)
                .forum(forum)
                .seq(MenuLevel.COMMENT)
                .build();
    }

    // 대댓글 작성 (2 depth)
    public static Commented writeForumReComment(Member member, Commented parentComment) {
        validateReComment(parentComment);

        return Commented.builder()
                .member(member)
                .forum(parentComment.getForum()) // 같은 forum
                .seq(MenuLevel.RE_COMMENT)
                .build();
    }

    public void increaseLikeCount() { this.likeCount++; }

    public void decreaseLikeCount() {
        state(this.likeCount > 0, "좋아요는 음수가 될 수 없습니다.");
        this.likeCount--;
    }

    public void increaseReportCount() { this.reportCount++; }


    /* 상태 판별 메서드 */
    public boolean isComment() {
        return this.seq == MenuLevel.COMMENT;
    }

    public boolean isReComment() {
        return this.seq == MenuLevel.RE_COMMENT;
    }


    /* 비즈니스 검증 메서드 */
    private static void validateReComment(Commented parentComment) {
        if (!parentComment.isComment()) {
            throw new IllegalArgumentException("대댓글은 댓글에만 작성할 수 있습니다.");
        }
    }
}
