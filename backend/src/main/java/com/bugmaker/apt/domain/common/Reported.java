package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
import com.bugmaker.apt.enums.report.ReportReason;
import com.bugmaker.apt.enums.report.ReportStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "reported",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reported_reporter_forum",
                        columnNames = {"reporter_id", "forum_id"}
                ),
                @UniqueConstraint(
                        name = "uk_reported_reporter_comment",
                        columnNames = {"reporter_id", "comment_id"}
                ),
                @UniqueConstraint(
                        name = "uk_reported_reporter_member",
                        columnNames = {"reporter_id","target_member_id"}
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("신고 테이블")
public class Reported extends BaseEntity {

    /* 신고 주체 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @Comment("회원 ID")
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    @Comment("토론 ID")
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @Comment("댓글 ID")
    private Commented commented;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    private Member targetMember; // 사용자를 신고함

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    /* 생성 팩토리 */
    public static Reported forForum(Member reporter, Forum forum, ReportReason reason) {
        return Reported.builder()
                .reporter(reporter)
                .forum(forum)
                .reason(reason)
                .status(ReportStatus.REGISTERED)
                .build();
    }

    public static Reported forComment(Member reporter, Commented commented, ReportReason reason) {
        return Reported.builder()
                .reporter(reporter)
                .commented(commented)
                .reason(reason)
                .status(ReportStatus.REGISTERED)
                .build();
    }

    public static Reported forMember(Member reporter, Member target, ReportReason reason) {
        return Reported.builder()
                .reporter(reporter)
                .targetMember(target)
                .reason(reason)
                .status(ReportStatus.REGISTERED)
                .build();
    }

    // 피신고자(targetMember) 가져오기
    public Member getReportedMember() {
        if (forum != null) return forum.getMember();
        if (commented != null) return commented.getMember();
        return targetMember;
    }

    /* 타입 판별 (신고 여부 확인) */
    public boolean isForumReport() {
        return forum != null;
    }

    public boolean isCommentReport() {
        return commented != null;
    }

    public boolean isMemberReport() {
        return targetMember != null;
    }

    /* 상태 판별 (관리자 페이지) */
    public boolean isRegistered() {
        return status == ReportStatus.REGISTERED;
    }

    public boolean isApproved() {
        return status == ReportStatus.APPROVED;
    }

    public boolean isRejected() {
        return status == ReportStatus.REJECTED;
    }

}
