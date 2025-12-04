package com.bugmaker.apt.service.member.oauth;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.dto.member.oauth.CustomOAuth2User;
import com.bugmaker.apt.dto.member.oauth.OAuth2UserInfo;
import com.bugmaker.apt.dto.member.oauth.OAuth2UserInfoFactory;
import com.bugmaker.apt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * OAuth2 로그인 처리 서비스
 * 로그인 시 자동으로 회원가입(join) 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final NicknameCreator nicknameCreator;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 제공자로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 제공자 정보 추출
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("OAuth2 로그인 시도 - Provider: {}, Attributes: {}", registrationId, attributes);

        // 3. 제공자별 사용자 정보 파싱
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();

        log.info("OAuth2 사용자 정보 - Provider: {}, ProviderId: {}, Email: {}", provider, providerId, email);

        // 4. 기존 회원 조회 또는 신규 회원가입(join)
        Member member = memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> joinNewMember(email, provider, providerId));

        // 5. nameAttributeKey를 application.yml에서 가져오기
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 6. CustomOAuth2User 반환
        return new CustomOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                nameAttributeKey,
                member.getId()
        );
    }

    /**
     * 신규 회원 가입(join)
     */
    private Member joinNewMember(String email, String provider, String providerId) {
        log.info("신규 회원 가입(join) - Email: {}, Provider: {}, ProviderId: {}", email, provider, providerId);

        Member newMember = Member.joinWithOAuth2(email, provider, providerId, nicknameCreator);
        return memberRepository.save(newMember);
    }
}
