package com.bugmaker.apt.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    SYSTEM("SYSTEM", "시스템", 1000, "시스템 자동 작업용"),

    ADMIN("ADMIN", "관리자", 100, "사용자 및 운영 관리"),

    USER("USER", "일반회원", 1, "일반 사용자"),

    GUEST("GUEST", "게스트", 0, "비회원/임시 사용자");

    private final String code;         // DB 저장용 코드
    private final String displayName;  // 화면 표시용 이름 (한글)
    private final int level;           // 권한 레벨 (높을수록 상위 권한)
    private final String description;  // 역할 설명
}
