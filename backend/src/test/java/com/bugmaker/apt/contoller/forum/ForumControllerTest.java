package com.bugmaker.apt.contoller.forum;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumFixture;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MemberFixture;
import com.bugmaker.apt.repository.ForumRepository;
import com.bugmaker.apt.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@RequiredArgsConstructor
class ForumControllerTest {

    final MockMvcTester mvcTester;
    final ObjectMapper objectMapper;
    final MemberRepository memberRepository;
    final ForumRepository forumRepository;
    final EntityManager entityManager;

    Member member;

    @BeforeEach
    void setUp() {
        Member request = Member.register(MemberFixture.createMemberRegisterRequest(), MemberFixture.nicknameCreator());
        memberRepository.save(request);

        member = memberRepository.findById(request.getId()).orElseThrow();
    }


    @Rollback
    @Test
    void postUp() throws JsonProcessingException, UnsupportedEncodingException {
        ForumCreateRequest forumRequest = ForumFixture.forumCreateRequest();
        String ForumJson = objectMapper.writeValueAsString(forumRequest);

        MvcTestResult result =
                mvcTester.post()
                        .uri("/api/forum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ForumJson)
                        .with(SecurityMockMvcRequestPostProcessors.user(member))
                        .exchange();

        assertThat(result)
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.forumId", value -> {
                    assertThat(value).isNotNull();
                })
                .hasPathSatisfying("$.title", value -> {
                    assertThat(value).isNotNull();
                });

        ForumCreateResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ForumCreateResponse.class);

        entityManager.flush();
        entityManager.clear();

        Forum forum = forumRepository.findById(response.forumId()).orElseThrow();

        assertThat(forum.getTitle()).isEqualTo(forumRequest.title());
        assertThat(forum.getMember()).isEqualTo(member);
    }

    @Test
    void update() throws UnsupportedEncodingException, JsonProcessingException {
        // 포럼 생성
        ForumCreateRequest forumCreateRequest = ForumFixture.forumCreateRequest();
        String ForumCreateJson = objectMapper.writeValueAsString(forumCreateRequest);
        MvcTestResult saveResult =
                mvcTester.post()
                        .uri("/api/forum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ForumCreateJson)
                        .with(SecurityMockMvcRequestPostProcessors.user(member))
                        .exchange();

        ForumCreateResponse saveResponse = objectMapper.readValue(saveResult.getResponse().getContentAsString(), ForumCreateResponse.class);

        assertThat(saveResponse.title()).isEqualTo("기본제목");

        entityManager.flush();
        entityManager.clear();

        Forum savedForum = forumRepository.findById(saveResponse.forumId()).orElseThrow();
        String savedForumTitle = savedForum.getTitle();

        // 포럼 수정
        ForumUpdateRequest updateRequest = new ForumUpdateRequest(savedForum.getId(), "수정된제목", "수정된 내용");
        String ForumUpdateJson = objectMapper.writeValueAsString(updateRequest);

        MvcTestResult updateResult =
                mvcTester.put()
                        .uri("/api/forum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ForumUpdateJson)
                        .exchange();

        ForumCreateResponse updateResponse = objectMapper.readValue(updateResult.getResponse().getContentAsString(), ForumCreateResponse.class);

        entityManager.flush();
        entityManager.clear();

        Forum updatedForum = forumRepository.findById(updateResponse.forumId()).orElseThrow();

        assertThat(updatedForum.getId()).isEqualTo(savedForum.getId());
        assertThat(updatedForum.getTitle()).isNotEqualTo(savedForumTitle);
    }

}