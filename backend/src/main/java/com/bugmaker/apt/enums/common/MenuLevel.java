package com.bugmaker.apt.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuLevel {

//    HOME(0, "홈"),

    LV1(10, "1단계"), // 뉴스, 시세트렌드(전국), 커뮤니티
    LV2(20, "2단계"), // 시/도
    LV3(30, "3단계"), // 구/군
    LV4(40, "4단계"), // 동

    COMMENT(100, "댓글"),
    RE_COMMENT(101, "대댓글");

    private final int value;
    private final String displayName;
}
