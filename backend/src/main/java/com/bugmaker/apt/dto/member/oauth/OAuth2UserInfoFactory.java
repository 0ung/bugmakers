package com.bugmaker.apt.dto.member.oauth;

import java.util.Map;

/** OAuth2 제공자별 UserInfo 객체를 생성하는 팩토리 */
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "naver" -> {
                // 네이버는 response 안에 실제 데이터가 있음
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                yield new NaverUserInfo(response);
            }
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
        };
    }
}
