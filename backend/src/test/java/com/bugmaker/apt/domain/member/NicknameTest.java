package com.bugmaker.apt.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {
    
    @Test
    void createNickname() {
        new Nickname("안녕");
        new Nickname("jkchoi");
        new Nickname("하하하하123");
    }

    @Test
    void createNicknameFail() {
        assertThatThrownBy(() -> new Nickname("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Nickname("가")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Nickname("너무길다너무길다너무길다")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Nickname("!@#$!@#$")).isInstanceOf(IllegalArgumentException.class);
    }

}