package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.constants.IllegalReason;
import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Illegal extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;  // 신고 당한 사람

    @Enumerated(STRING)
    @Column(nullable = false)
    private IllegalReason reason;  // 신고 사유 '6. 기타' 시 '상세 설명' 선택 추가 가능

    @Column(columnDefinition = "TEXT")
    private String description;  // 상세 설명

    private Long newsId;

    private Long forumId;

    @Enumerated(STRING)
    @Column(nullable = false)
    private IllegalStatus illegalStatus;  // 신고 상태


    // 팩토리 메서드 - 뉴스 신고
    public static Illegal reportNews(
            Long memberId,
            Long newsId,
            IllegalReason reason,
            String description) {
        Illegal illegal = new Illegal();

        illegal.memberId = memberId;
        illegal.newsId = newsId;
        illegal.reason = reason;
        illegal.description = description;
        illegal.illegalStatus = IllegalStatus.REGISTERED;

        return illegal;
    }

    // 팩토리 메서드 - 토론 신고
    public static Illegal reportForum(
            Long memberId,
            Long forumId,
            IllegalReason reason,
            String description) {
        Illegal illegal = new Illegal();

        illegal.memberId = memberId;
        illegal.forumId = forumId;
        illegal.reason = reason;
        illegal.description = description;
        illegal.illegalStatus = IllegalStatus.REGISTERED;

        return illegal;
    }

    // 비즈니스 메서드 - 대기중으로 변경
    public void markAsPending() {
        this.illegalStatus = IllegalStatus.PENDING;
    }

    // 비즈니스 메서드 - 승인 (신고 처리 완료)
    public void approve() {
        this.illegalStatus = IllegalStatus.APPROVED;
    }

    // 비즈니스 메서드 - 거부 (신고 무효)
    public void reject() {
        this.illegalStatus = IllegalStatus.REJECTED;
    }

    // 타입 확인 메서드
    public boolean isNewsReport() {
        return newsId != null;
    }

    public boolean isForumReport() {
        return forumId != null;
    }

    // 상태 확인 메서드
    public boolean isRegistered() {
        return this.illegalStatus == IllegalStatus.REGISTERED;
    }

    public boolean isPending() {
        return this.illegalStatus == IllegalStatus.PENDING;
    }

    public boolean isApproved() {
        return this.illegalStatus == IllegalStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.illegalStatus == IllegalStatus.REJECTED;
    }
}
