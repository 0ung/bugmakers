package com.bugmaker.apt.service.member;

import com.bugmaker.apt.constants.MemberRole;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 회원 ID로 조회
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));
    }

    // 회원 정지
    @Transactional
    public void deactivateMember(Long memberId) {
        Member member = findById(memberId);

        if (!member.isActive()) {
            log.warn("이미 비활성화된 회원입니다 - MemberId: {}", memberId);
            return;
        }

        member.deactivate();
        log.info("회원 정지 완료 - MemberId: {}, Nickname: {}", memberId, member.getNickname());
    }

    // 회원 활성화 (정지 해제)
    @Transactional
    public void activateMember(Long memberId) {
        Member member = findById(memberId);
        member.activate();
        log.info("회원 활성화 완료 - MemberId: {}, Nickname: {}", memberId, member.getNickname());
    }

    // 관리자 권한 확인
    public boolean isAdmin(Long memberId) {
        Member member = findById(memberId);
        return member.getMemberRole() == MemberRole.ADMIN;
    }

    // 관리자 권한 검증 (예외 발생)
    public void validateAdmin(Long memberId) {
        if (!isAdmin(memberId)) {
            throw new IllegalStateException("관리자 권한이 필요합니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            return memberRepository.findById(Long.parseLong(username)).orElseThrow();
        }catch (NumberFormatException e){
            log.error("user Id Parser Error");
        }
        return null;
    }
}
