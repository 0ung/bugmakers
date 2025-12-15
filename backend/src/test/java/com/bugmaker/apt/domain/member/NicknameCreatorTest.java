package com.bugmaker.apt.domain.member;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NicknameCreatorTest {

    @Test
    void generateNickname() {
        MemberRegisterRequest registerRequest = MemberFixture.createMemberRegisterRequest("bugmakers@naver.com");

        Member member = Member.register(registerRequest, () -> "금붕어");

        assertThat(member.getNickname()).isNotNull();
        assertThat(member.getNickname()).isEqualTo("금붕어");
    }
}