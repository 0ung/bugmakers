package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.enums.member.Status;
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