package com.bugmaker.apt.dto.member;

/** 로그인 응답 DTO */
public record LoginResponse (
        String token,
        Long memberId,
        String email,
        String nickname
) {
}
