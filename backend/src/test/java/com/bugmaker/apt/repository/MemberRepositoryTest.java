package com.bugmaker.apt.repository;

import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.member.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static com.bugmaker.apt.domain.member.MemberFixture.createMemberRegisterRequest;
import static com.bugmaker.apt.domain.member.MemberFixture.nicknameCreator;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(locations = "classpath:application-test.yml")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void save() {
        Member member = Member.register(createMemberRegisterRequest(), nicknameCreator());

        assertThat(member.getId()).isNull();

        memberRepository.save(member);

        assertThat(member.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var found = memberRepository.findById(member.getId()).orElseThrow();

        assertThat(found.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(found.getCreatedDate()).isNotNull();
        assertThat(found.getEmail().address()).isEqualTo(member.getEmail().address());
    }

    @Test
    void findByNicknameContaining() {
        Member saveMember1 = Member.register(createMemberRegisterRequest(), nicknameCreator()); // 기존 닉네임 : 라이언일병
        Member saveMember2 = Member.register(createMemberRegisterRequest(), nicknameCreator("춘식이"));
        Member saveMember3 = Member.register(createMemberRegisterRequest(), nicknameCreator("라이언병장"));
        Member saveMember4 = Member.register(createMemberRegisterRequest(), nicknameCreator("상병라이언"));
        Member saveMember5 = Member.register(createMemberRegisterRequest(), nicknameCreator("상라이병언"));

        memberRepository.save(saveMember1);
        memberRepository.save(saveMember2);
        memberRepository.save(saveMember3);
        memberRepository.save(saveMember4);
        memberRepository.save(saveMember5);

        entityManager.flush();
        entityManager.clear();

        List<Member> found = memberRepository.findByNicknameContaining("라이언");

        assertThat(found).hasSize(3);
        assertThat(found).allMatch(member -> member.getNickname().contains("라이언"));
    }

}