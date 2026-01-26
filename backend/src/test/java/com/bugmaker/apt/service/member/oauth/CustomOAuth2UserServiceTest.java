package com.bugmaker.apt.service.member.oauth;

import com.bugmaker.apt.common.exception.custom.MemberDeactivatedException;
import com.bugmaker.apt.constants.IllegalReason;
import com.bugmaker.apt.enums.Status;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.repository.member.MemberRepository;
import com.bugmaker.apt.service.illegal.IllegalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CustomOAuth2UserServiceTest {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IllegalService illegalService;

    @Autowired
    private NicknameCreator nicknameCreator;

    @Test
    @DisplayName("신규 회원 OAuth2 로그인 성공 - 자동 회원가입")
    void loadUser_NewMember_Success() {
        // given
        OAuth2UserRequest userRequest = createGoogleOAuth2UserRequest("new-user-id", "newuser@gmail.com");

        // when
        OAuth2User oauth2User = customOAuth2UserService.loadUser(userRequest);

        // then
        assertThat(oauth2User).isNotNull();

        // 회원이 자동 생성되었는지 확인
        Member member = memberRepository.findByProviderAndProviderId("google", "new-user-id").orElseThrow();
//        assertThat(member.getEmail().address()).isEqualTo("newuser@gmail.com"); //record 에서
        assertThat(member.getEmail().getAddress()).isEqualTo("newuser@gmail.com"); //class로 변경함
        assertThat(member.isActive()).isTrue();
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("기존 활성 회원 OAuth2 로그인 성공")
    void loadUser_ActiveMember_Success() {
        // given
        Member existingMember = Member.joinWithOAuth2(
                "existing@gmail.com",
                "google",
                "existing-user-id",
                nicknameCreator
        );
        memberRepository.save(existingMember);

        OAuth2UserRequest userRequest = createGoogleOAuth2UserRequest("existing-user-id", "existing@gmail.com");

        // when
        OAuth2User oauth2User = customOAuth2UserService.loadUser(userRequest);

        // then
        assertThat(oauth2User).isNotNull();
    }

    @Test
    @DisplayName("정지된 회원 OAuth2 로그인 차단 - MemberDeactivatedException 발생")
    void loadUser_DeactivatedMember_ThrowsException() {
        // given
        // 1. 회원 생성
        Member member = Member.joinWithOAuth2(
                "deactivated@gmail.com",
                "google",
                "deactivated-user-id",
                nicknameCreator
        );
        member = memberRepository.save(member);

        // 2. 신고 등록 및 승인 → 회원 자동 정지
        var illegal = illegalService.reportNews(
                member.getId(),
                1L,
                IllegalReason.ABUSIVE_LANGUAGE,
                "욕설"
        );
        illegalService.approveReport(illegal.getId());

        // 3. 회원이 정지되었는지 확인
        Member deactivatedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertThat(deactivatedMember.isActive()).isFalse();
        assertThat(deactivatedMember.getStatus()).isEqualTo(Status.DEACTIVE);

        OAuth2UserRequest userRequest = createGoogleOAuth2UserRequest("deactivated-user-id", "deactivated@gmail.com");

        // when & then
        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(MemberDeactivatedException.class)
                .hasMessageContaining("계정이 정지되었습니다");
    }

    @Test
    @DisplayName("정지 후 활성화된 회원은 다시 로그인 가능")
    void loadUser_ReactivatedMember_Success() {
        // given
        // 1. 회원 생성 및 정지
        Member member = Member.joinWithOAuth2(
                "reactivated@gmail.com",
                "google",
                "reactivated-user-id",
                nicknameCreator
        );
        member = memberRepository.save(member);
        member.deactivate();
        memberRepository.save(member);

        // 2. 회원 재활성화
        member.activate();
        memberRepository.save(member);

        OAuth2UserRequest userRequest = createGoogleOAuth2UserRequest("reactivated-user-id", "reactivated@gmail.com");

        // when
        OAuth2User oauth2User = customOAuth2UserService.loadUser(userRequest);

        // then
        assertThat(oauth2User).isNotNull();
    }

    // ========== 헬퍼 메서드 ==========

    private OAuth2UserRequest createGoogleOAuth2UserRequest(String providerId, String email) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("google")
                .clientId("test-client-id")
                .clientSecret("test-client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/google")
                .scope("email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "test-access-token",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", providerId);
        attributes.put("email", email);
        attributes.put("name", "Test User");

        return new OAuth2UserRequest(clientRegistration, accessToken, attributes);
    }

}