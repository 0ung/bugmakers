package com.bugmaker.apt.domain.member;

public class MemberFixture {
    public static MemberRegisterRequest createMemberRegisterRequest(String email) {
        return new MemberRegisterRequest(email);
    }

    public static MemberRegisterRequest createMemberRegisterRequest() {
        return createMemberRegisterRequest("bugmakers@naver.com");
    }

    public static NicknameCreator nicknameCreator() {
        return new NicknameCreator() {
            @Override
            public String generate() {
                return "라이언일병";
            }
        };
    }

    public static NicknameCreator nicknameCreator(String nickname) {
        return new NicknameCreator() {
            @Override
            public String generate() {
                return nickname;
            }
        };
    }
}
