package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumFixture;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MemberFixture;
import com.bugmaker.apt.repository.forum.ForumRepository;
import com.bugmaker.apt.repository.member.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ForumRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ForumRepository forumRepository;

    @Autowired
    EntityManager entityManager;


    @Test
    void forum_postUp() {
        Member member = Member.register(MemberFixture.createMemberRegisterRequest(), MemberFixture.nicknameCreator());
        Member savedMember = memberRepository.save(member);
        Forum forum = Forum.postUp(ForumFixture.forumCreateRequest(), savedMember);

        assertThat(forum.getId()).isNull();

        forumRepository.save(forum);

        assertThat(forum.getId()).isNotNull();

        entityManager.flush();
        entityManager.clear();

        var found = forumRepository.findById(forum.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(forum.getId());
        assertThat(found.getMember()).isEqualTo(forum.getMember());
        assertThat(found.getTitle()).isEqualTo(forum.getTitle());
    }
}