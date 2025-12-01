package com.bugmaker.apt.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentSeq {

    COMMENT(1, "댓글"),
    RE_COMMENT(2, "대댓글");

    private final int value;
    private final String displayName;
}
