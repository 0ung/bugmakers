package com.bugmaker.apt.member.entity;

import com.bugmaker.apt.constants.RoleEnum;
import com.bugmaker.apt.constants.YnEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true, length = 50)
    private String memberId;  // [PK] 로그인 ID

    @ToString.Exclude
    @Column(nullable = false)
    private String password;  // 비밀번호

    @Transient
    @ToString.Exclude
    private String passwordConfirm;  // 비밀번호 확인

    @Column(length = 50)
    private String name;  // 이름 (선택)

    @Column(length = 20)
    private String phone;  // 전화번호 (선택)

    @Column(length = 100)
    private String email;  // 이메일 (선택)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleEnum role = RoleEnum.USER;  // 역할 (기본값: USER)

    @Enumerated(EnumType.STRING)
    @Column(name = "del_yn", nullable = false, length = 1)
    private YnEnum delYn = YnEnum.N;  // 삭제 여부 (기본값: N)

    private LocalDateTime regDt;  // 등록일시
    private LocalDateTime uptDt;  // 수정일시

    @PrePersist
    protected void onCreate() {
        regDt = LocalDateTime.now();
        uptDt = LocalDateTime.now();
        if (role == null) {
            role = RoleEnum.USER;
        }
        if (delYn == null) {
            delYn = YnEnum.N;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        uptDt = LocalDateTime.now();
    }
}
