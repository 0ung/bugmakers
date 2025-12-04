package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.member.Member;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {
    Member save(Member member);

    Optional<Member> findById(Long memberId);

    List<Member> findByNicknameContaining(String nickname);

    // OAuth2 관련 메서드
    Optional<Member> findByProviderAndProviderId(String provider, String providerId);
}
