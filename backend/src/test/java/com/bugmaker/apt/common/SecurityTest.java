package com.bugmaker.apt.common;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MemberFixture;
import com.bugmaker.apt.domain.member.MemberRegisterRequest;
import com.bugmaker.apt.domain.member.NicknameCreator;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

class SecurityTest {
    @WithMockUser(username = "1", roles = "ADMIN")
    @Test
    void mockTest() {
        MemberRegisterRequest registerRequest = MemberFixture.createMemberRegisterRequest("bugmakers@naver.com");
        NicknameCreator nickname = MemberFixture.nicknameCreator("강아지");
        Member member = Member.register(registerRequest, nickname);
    }


}
