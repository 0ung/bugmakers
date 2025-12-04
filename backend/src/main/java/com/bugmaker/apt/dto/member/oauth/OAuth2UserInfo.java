package com.bugmaker.apt.dto.member.oauth;

/** OAuth2 제공자별 사용자 정보를 추상화하는 인터페이스 */
public interface OAuth2UserInfo {
    String getProviderId();  // OAuth2 제공자의 고유 ID

    String getProvider();    // google, naver, kakao

    String getEmail();

    String getName();
}
