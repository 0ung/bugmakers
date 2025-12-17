package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.tag.Tag;
import com.bugmaker.apt.domain.tag.TagFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.bugmaker.apt.domain.member.MemberFixture.createMemberRegisterRequest;
import static com.bugmaker.apt.domain.member.MemberFixture.nicknameCreator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForumTest {
    Member member;

    Forum forum;

    @BeforeEach
    void setUp() {
        member = Member.register(createMemberRegisterRequest(), nicknameCreator());

        forum = Forum.postUp(new ForumCreateRequest("제목입니다", "내용입니다"), member);
    }

    @Test
    void postUp() {
        assertThat(forum.getTitle()).isEqualTo("제목입니다");
        assertThat(forum.getContent()).isEqualTo("내용입니다");
        assertThat(forum.getMember().getNickname()).isEqualTo(member.getNickname());

        assertThat(forum.getViewCount()).isEqualTo(0L);
        assertThat(forum.getHeartCount()).isEqualTo(0L);
        assertThat(forum.getReportCount()).isEqualTo(0L);
    }
    
    @Test
    void addForumTagRelation() {
        Tag tag = TagFixture.createTag();
        Tag tag2 = TagFixture.createTag("경기도");
        Tag tag3 = TagFixture.createTag("인천");
        ForumTagRelationRequest request = new ForumTagRelationRequest(forum, tag);
        ForumTagRelationRequest request2 = new ForumTagRelationRequest(forum, tag2);
        ForumTagRelationRequest request3 = new ForumTagRelationRequest(forum, tag3);
        ForumTagRelation relation = ForumTagRelation.relation(request);
        ForumTagRelation relation2 = ForumTagRelation.relation(request2);
        ForumTagRelation relation3 = ForumTagRelation.relation(request3);

        forum.addForumTagRelation(relation);
        forum.addForumTagRelation(relation2);
        forum.addForumTagRelation(relation3);

        List<ForumTagRelation> forumTagRelationList = forum.getForumTagRelationList();

        System.out.println("포럼 제목 : " + forum.getTitle());
        System.out.println("포럼 작성자 메일 : " + forum.getMember().getEmail().address());
        System.out.println("포럼 작성자 닉네임 : " + forum.getMember().getNickname());

        for (ForumTagRelation forumTagRelation : forumTagRelationList) {
            System.out.println("포럼 태그 : " + forumTagRelation.getTag().getName());
        }

        assertThat(forumTagRelationList.size()).isEqualTo(3);
    }

    @Test
    void delete() {
        assertThat(forum.isActive()).isTrue();

        forum.delete();

        assertThat(forum.isActive()).isFalse();
    }

    @Test
    void increaseViewCountAndReportCount() {
        assertThat(forum.getViewCount()).isEqualTo(0L);
        assertThat(forum.getReportCount()).isEqualTo(0L);

        // 조회수
        forum.increaseViewCount();
        forum.increaseViewCount();
        forum.increaseViewCount();

        assertThat(forum.getViewCount()).isEqualTo(3L);

        // 신고 누적수
        forum.increaseReportCount();

        assertThat(forum.getReportCount()).isEqualTo(1L);
    }

    @Test
    void decreaseHeartCountSuccessAndFail() {
        assertThat(forum.getHeartCount()).isEqualTo(0L);

        forum.increaseHeartCount();
        forum.increaseHeartCount();
        assertThat(forum.getHeartCount()).isEqualTo(2L);

        forum.decreaseHeartCount();
        assertThat(forum.getHeartCount()).isEqualTo(1L);

        forum.decreaseHeartCount();

        assertThatThrownBy(() -> forum.decreaseHeartCount())
        .isInstanceOf(IllegalStateException.class);
    }


}