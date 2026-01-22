package com.bugmaker.apt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    ADMIN("관리자","admin"),

    USER("일반 회원","user");

    private final String displayName;
    //시큐리티에 권한 부여를 위해 생성
    private final String name;
}
