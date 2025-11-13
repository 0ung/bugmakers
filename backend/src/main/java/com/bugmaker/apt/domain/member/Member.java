package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.domain.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.Assert.state;


@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
public class Member extends AbstractEntity {
    @NaturalId
    @Embedded
    private Email email;

    private String passwordHash;

    private String name;

    private String phone;

    @OneToOne
    private MemberDetail detail;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public static Member register(MemberRegisterRequest registerRequest, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.email = new Email(registerRequest.email());
        member.passwordHash = passwordEncoder.encode(registerRequest.password());
        member.name = registerRequest.name();
        member.phone = registerRequest.phone();

        member.detail = MemberDetail.create();

        member.role = MemberRole.USER;
        member.status = MemberStatus.ACTIVE;

        return member;
    }

    public void deactivate() {
        state(status == MemberStatus.ACTIVE, "ACTIVE 상태에서만 탈퇴가 가능합니다.");

        this.status = MemberStatus.DEACTIVATED;
    }

    public void updateInfo(MemberInfoUpdateRequest updateRequest) {
        state(getStatus() == MemberStatus.ACTIVE, "ACTIVE상태가 아니면 정보수정이 불가능합니다");

        this.detail.updateInfo(updateRequest);
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
