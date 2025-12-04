package com.bugmaker.apt.dto.member.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth2 인증된 사용자 정보를 담는 클래스
 * JWT 생성을 위해 memberId를 포함
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private final Long memberId;

    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            Long memberId) {
        super(authorities, attributes, nameAttributeKey);
        this.memberId = memberId;
    }
}
