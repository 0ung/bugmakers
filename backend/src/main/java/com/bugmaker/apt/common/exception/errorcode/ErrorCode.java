package com.bugmaker.apt.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    BAD_REQUEST(400, HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),

    INCLUDE_IMPROPER_WORDS(403, HttpStatus.FORBIDDEN, "부적절한 단어가 포함되어 있습니다."),
    DEACTIVATED_MEMBER(403, HttpStatus.FORBIDDEN, "계정이 정지되었습니다. 관리자에게 문의하세요."),

    NOT_FOUND_END_POINT(404, HttpStatus.NOT_FOUND, "존재하지 않는 API 입니다"),
    ENTITY_NOT_FOUND(404,HttpStatus.NOT_FOUND, "요청하신 엔티티를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(404,HttpStatus.NOT_FOUND, "요청하신 회원을 찾을 수 없습니다."),
    NEWS_NOT_FOUND(404, HttpStatus.NOT_FOUND, "요청하신 뉴스를 찾을 수 없습니다."),
    NOT_LIKED_YET(404, HttpStatus.NOT_FOUND, "좋아요를 누르지 않은 뉴스입니다."),
    NOT_FAVORITED_YET(404, HttpStatus.NOT_FOUND, "즐겨찾기하지 않은 뉴스입니다."),

    ALREADY_LIKED(409, HttpStatus.CONFLICT, "이미 좋아요를 누른 뉴스입니다."),
    ALREADY_FAVORITED(409, HttpStatus.CONFLICT, "이미 즐겨찾기한 뉴스입니다."),


    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
