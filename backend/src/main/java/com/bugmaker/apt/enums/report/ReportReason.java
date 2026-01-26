package com.bugmaker.apt.enums.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {

    ABUSIVE_LANGUAGE(1, "욕설 및 비방"),

    FALSE_INFORMATION(2, "허위 정보"),

    SPAM_ADVERTISEMENT(3, "스팸 또는 광고"),

    PRIVACY_VIOLATION(4, "개인정보 노출"),

    OBSCENE_CONTENT(5, "음란성 게시물"),

    ETC(6, "기타");

    private final int value;
    private final String displayName;
}
