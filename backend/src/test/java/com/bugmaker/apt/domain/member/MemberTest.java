package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.constants.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.bugmaker.apt.domain.member.MemberFixture.createMemberRegisterRequest;
import static com.bugmaker.apt.domain.member.MemberFixture.nicknameCreator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {
    Member member;

    @BeforeEach
    void setUp() {
        member = Member.register(createMemberRegisterRequest(), nicknameCreator());
    }

//    @Test
//    void register() {
//        var registerRequest = new MemberRegisterRequest("bugmakers@naver.com");
//
//        Member member = Member.register(registerRequest, new NicknameCreator() {
//            @Override
//            public String generate() {
//                return "매서운 호랑이";
//            }
//        });
//
//        assertThat(member.getNickname()).isEqualTo("매서운 호랑이");
//        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
//        assertThat(member.getMemberRole()).isEqualTo(MemberRole.USER);
//    }

    @Test
    void activate() {
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
        member.deactivate();

        member.activate();

        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void activateFail() {
        assertThatThrownBy(() -> member.activate()).isInstanceOf(IllegalStateException.class);

        member.deactivate();
        member.activate();

        assertThatThrownBy(() -> member.activate()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deactivate() {
        member.deactivate();

        assertThat(member.getStatus()).isEqualTo(Status.DEACTIVE);
    }

    @Test
    void deactivateFail() {
        member.deactivate();

        assertThatThrownBy(() -> member.deactivate()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void isActive() {
        assertThat(member.isActive()).isTrue();

        member.deactivate();

        assertThat(member.isActive()).isFalse();
    }
}