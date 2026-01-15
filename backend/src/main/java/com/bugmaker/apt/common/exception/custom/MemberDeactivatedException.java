package com.bugmaker.apt.common.exception.custom;

import com.bugmaker.apt.common.exception.errorcode.ErrorCode;

/**
 * 계정이 정지된 회원의 로그인 시도 예외
 * */
public class MemberDeactivatedException extends CustomException {
    public MemberDeactivatedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
