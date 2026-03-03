package com.bugmaker.apt.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {

    REGISTERED(1, "등록됨"),

    PENDING(2, "대기중"),

    APPROVED(3, "승인됨"),

    REJECTED(4, "거부됨");

    private final int value;
    private final String displayName;
}
