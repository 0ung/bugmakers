package com.bugmaker.apt.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
/*
수정 : record -> class 변경
원인 : IllegalArgumentException Java compile Err 발생
      ㄴ Hibernate / Spring Data JPA / QueryDSL annotation processor 를 넣었더니,
         record를 @Embeddable 타입으로 완전히 해석 못함
수정 근거 : JPA 표준
          record는 “표현용 데이터”에 쓰고, JPA가 관리하는 객체(Entity / Embeddable)는 class로 만든다.
*/
public class Email {
    @Column(name = "email", nullable = false, unique = true)
    private String address;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public Email(String address) {
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("이메일 형식이 바르지 않습니다 ==> " + address);
        }
        this.address = address;
    }
}
