package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.constants.MemberRole;
import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.Assert.state;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseEntity implements UserDetails {
//    @Column(nullable = false, unique = true, length = 100)
    @Embedded
    private Email email;

//    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(STRING)
    private MemberRole memberRole;

    @Enumerated(STRING)
    private Status status;

    private LocalDateTime deactivatedDate;

    // OAuth2 관련 필드
    private String provider; // google, naver, kakao
    private String providerId; // OAuth2 제공자의 고유 ID (카카오의 경우 id 필드)


    public static Member register(MemberRegisterRequest registerRequest, NicknameCreator nicknameCreator) {
        Member member = new Member();

        member.email = new Email(registerRequest.email());
        member.nickname = nicknameCreator.generate();
        member.memberRole = MemberRole.USER;
        member.status = Status.ACTIVE;

        return member;
    }

    // OAuth2 회원가입용 팩토리 메서드
    public static Member joinWithOAuth2(String email, String provider, String providerId, NicknameCreator nicknameCreator) {
        Member member = new Member();
        
        member.email = new Email(email);
        member.nickname = nicknameCreator.generate();
        member.memberRole = MemberRole.USER;
        member.status = Status.ACTIVE;
        member.provider = provider;
        member.providerId = providerId;
        
        return member;
    }

    // 비즈니스 메서드
    public void activate() {
        state(status == Status.DEACTIVE, "비활성화 상태의 계정만 활성화 시킬 수 있습니다.");

        this.status = Status.ACTIVE;
        this.deactivatedDate = null;
    }

    public void deactivate() {
        state(status == Status.ACTIVE, "활성 상태의 계정만 비활성화 시킬 수 있습니다.");

        this.status = Status.DEACTIVE;
        this.deactivatedDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+this.memberRole.getName()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.getId().toString();
    }
}