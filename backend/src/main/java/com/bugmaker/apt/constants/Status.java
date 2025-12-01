package com.bugmaker.apt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ACTIVE("활성"),

    DEACTIVE("비활성");

    private final String displayName;
}
