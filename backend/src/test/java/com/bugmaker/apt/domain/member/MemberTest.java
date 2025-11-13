package com.bugmaker.apt.domain.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class MemberTest {
    Member member;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = createPasswordEncoder();
        this.member = Member.register(createMemberRegisterRequest(), passwordEncoder);
    }

    @Test
    void registerMember() {
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
        assertThat(member.getDetail().getRegisteredAt()).isNotNull();
    }

    @Test
    void deactivate() {
        member.deactivate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @Test
    void deactivateFail() {
        member.deactivate();

        assertThatThrownBy(member::deactivate).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void isActive() {
        assertThat(member.isActive()).isTrue();

        member.deactivate();

        assertThat(member.isActive()).isFalse();
    }

    @Test
    void updateInfo() {
        assertThat(member.getDetail().getNickname()).isNull();
        assertThat(member.getDetail().getIntroduction()).isNull();
        var request = new MemberInfoUpdateRequest("모스콧", "자기소개입니다");

        member.updateInfo(request);

        assertThat(member.getDetail().getNickname()).isNotNull();
        assertThat(member.getDetail().getIntroduction()).isEqualTo("자기소개입니다");
    }



    // ===================== Fixture ======================= //
    private PasswordEncoder createPasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase() + "!@#";
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return encode(password).equals(passwordHash);
            }
        };
    }

    private MemberRegisterRequest createMemberRegisterRequest() {
        return new MemberRegisterRequest("jkchoi@naver.com", "bugMakers", "최준근", "010-1234-1234");
    }


}