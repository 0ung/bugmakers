package com.bugmaker.apt.common.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/** 계정이 정지된 회원의 로그인 시도 예외 */
public class MemberDeactivatedException extends OAuth2AuthenticationException {

    public MemberDeactivatedException(String message) {
        super(new OAuth2Error("member_deactivated"), message);
    }
}
