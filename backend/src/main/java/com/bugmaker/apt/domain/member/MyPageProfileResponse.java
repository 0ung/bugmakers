package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.constants.MemberRole;
import com.bugmaker.apt.constants.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MyPageProfileResponse(
        Long memberId,
        String email,
        String nickname,
        MemberRole memberRole,
        Status status,
        String provider,
        LocalDateTime createdDate,
        LocalDateTime deactivatedDate
) {
    public static MyPageProfileResponse of(Member member) {
        return MyPageProfileResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail().address())
                .nickname(member.getNickname())
                .memberRole(member.getMemberRole())
                .status(member.getStatus())
                .provider(member.getProvider())
                .createdDate(member.getCreatedDate())
                .deactivatedDate(member.getDeactivatedDate())
                .build();
    }
}
