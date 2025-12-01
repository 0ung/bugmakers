package com.bugmaker.apt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    ADMIN("관리자"),

    USER("일반 사용자"),

    GUEST("게스트");

    private final String displayName;
}
