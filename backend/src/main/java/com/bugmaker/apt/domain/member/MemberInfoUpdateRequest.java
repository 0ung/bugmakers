package com.bugmaker.apt.domain.member;

public record MemberInfoUpdateRequest(
        String nickname,
        String introduction
) {
}
