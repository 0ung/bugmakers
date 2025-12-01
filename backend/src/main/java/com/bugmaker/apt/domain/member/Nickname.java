package com.bugmaker.apt.domain.member;

import java.util.regex.Pattern;

public record Nickname(String nickname) {

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,8}$");

    public Nickname {
        if (nickname == null ||
                (!nickname.isEmpty() && !NICKNAME_PATTERN.matcher(nickname).matches())) {
            throw new IllegalArgumentException("닉네임 형식이 바르지 않습니다. ==> " + nickname);
        }

        if (nickname.length() < 2 || nickname.length() > 8) throw new IllegalArgumentException("닉네임은 2 ~ 10자 사이만 가능합니다. ==> " + nickname);
    }
}
