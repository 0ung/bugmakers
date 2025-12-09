package com.bugmaker.apt.service.illegal;

import com.bugmaker.apt.constants.IllegalReason;
import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.common.Illegal;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.repository.IllegalRepository;
import com.bugmaker.apt.repository.MemberRepository;
import com.bugmaker.apt.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class IllegalServiceTest {

    @Autowired
    private IllegalService illegalService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private IllegalRepository illegalRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NicknameCreator nicknameCreator;

    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = Member.joinWithOAuth2(
                "test@example.com",
                "google",
                "test-provider-id",
                nicknameCreator
        );
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("뉴스 신고 등록 성공")
    void reportNews_Success() {
        // given
        Long newsId = 1L;
        IllegalReason reason = IllegalReason.ABUSIVE_LANGUAGE;
        String description = "욕설이 포함되어 있습니다.";

        // when
        Illegal illegal = illegalService.reportNews(
                testMember.getId(),
                newsId,
                reason,
                description
        );

        // then
        assertThat(illegal).isNotNull();
        assertThat(illegal.getMemberId()).isEqualTo(testMember.getId());
        assertThat(illegal.getNewsId()).isEqualTo(newsId);
        assertThat(illegal.getReason()).isEqualTo(reason);
        assertThat(illegal.getDescription()).isEqualTo(description);
        assertThat(illegal.getIllegalStatus()).isEqualTo(IllegalStatus.REGISTERED);
        assertThat(illegal.isNewsReport()).isTrue();
        assertThat(illegal.isForumReport()).isFalse();
    }

    @Test
    @DisplayName("토론 신고 등록 성공")
    void reportForum_Success() {
        // given
        Long forumId = 1L;
        IllegalReason reason = IllegalReason.SPAM_ADVERTISEMENT;
        String description = "광고성 게시물입니다.";

        // when
        Illegal illegal = illegalService.reportForum(
                testMember.getId(),
                forumId,
                reason,
                description
        );

        // then
        assertThat(illegal).isNotNull();
        assertThat(illegal.getMemberId()).isEqualTo(testMember.getId());
        assertThat(illegal.getForumId()).isEqualTo(forumId);
        assertThat(illegal.getReason()).isEqualTo(reason);
        assertThat(illegal.getIllegalStatus()).isEqualTo(IllegalStatus.REGISTERED);
        assertThat(illegal.isNewsReport()).isFalse();
        assertThat(illegal.isForumReport()).isTrue();
    }

    @Test
    @DisplayName("신고 승인 시 회원이 자동으로 정지됨")
    void approveReport_DeactivatesMember() {
        // given
        Illegal illegal = illegalService.reportNews(
                testMember.getId(),
                1L,
                IllegalReason.ABUSIVE_LANGUAGE,
                "욕설"
        );

        // 회원이 처음에는 활성 상태
        assertThat(testMember.isActive()).isTrue();
        assertThat(testMember.getStatus()).isEqualTo(Status.ACTIVE);

        // when
        illegalService.approveReport(illegal.getId());

        // then
        // 신고가 승인됨
        Illegal approvedIllegal = illegalRepository.findById(illegal.getId()).orElseThrow();
        assertThat(approvedIllegal.getIllegalStatus()).isEqualTo(IllegalStatus.APPROVED);
        assertThat(approvedIllegal.isApproved()).isTrue();

        // 회원이 정지됨
        Member deactivatedMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(deactivatedMember.isActive()).isFalse();
        assertThat(deactivatedMember.getStatus()).isEqualTo(Status.DEACTIVE);
        assertThat(deactivatedMember.getDeactivatedDate()).isNotNull();
    }

    @Test
    @DisplayName("신고 거부 시 회원은 정지되지 않음")
    void rejectReport_DoesNotDeactivateMember() {
        // given
        Illegal illegal = illegalService.reportNews(
                testMember.getId(),
                1L,
                IllegalReason.FALSE_INFORMATION,
                "허위정보"
        );

        // when
        illegalService.rejectReport(illegal.getId());

        // then
        // 신고가 거부됨
        Illegal rejectedIllegal = illegalRepository.findById(illegal.getId()).orElseThrow();
        assertThat(rejectedIllegal.getIllegalStatus()).isEqualTo(IllegalStatus.REJECTED);
        assertThat(rejectedIllegal.isRejected()).isTrue();

        // 회원은 여전히 활성 상태
        Member activeMember = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(activeMember.isActive()).isTrue();
        assertThat(activeMember.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("여러 신고 승인해도 회원은 한 번만 정지됨 (중복 처리 방지)")
    void approveMultipleReports_DeactivatesOnce() {
        // given
        Illegal illegal1 = illegalService.reportNews(testMember.getId(), 1L, IllegalReason.ABUSIVE_LANGUAGE, "욕설1");
        Illegal illegal2 = illegalService.reportNews(testMember.getId(), 2L, IllegalReason.ABUSIVE_LANGUAGE, "욕설2");

        // when
        illegalService.approveReport(illegal1.getId());
        illegalService.approveReport(illegal2.getId()); // 이미 정지된 상태에서 다시 호출

        // then
        Member member = memberRepository.findById(testMember.getId()).orElseThrow();
        assertThat(member.isActive()).isFalse();
        assertThat(member.getStatus()).isEqualTo(Status.DEACTIVE);
    }

}