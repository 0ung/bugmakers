package com.bugmaker.apt.service.illegal;

import com.bugmaker.apt.constants.IllegalReason;
import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.domain.common.Illegal;
import com.bugmaker.apt.repository.IllegalRepository;
import com.bugmaker.apt.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllegalService {

    private final IllegalRepository illegalRepository;
    private final MemberService memberService;

    // 뉴스 신고 등록
    @Transactional
    public Illegal reportNews(Long memberId, Long newsId, IllegalReason reason, String description) {
        log.info("뉴스 신고 등록 - MemberId: {}, NewsId: {}, Reason: {}", memberId, newsId, reason);

        Illegal illegal = Illegal.reportNews(memberId, newsId, reason, description);
        return illegalRepository.save(illegal);
    }

    // 토론 신고 등록
    @Transactional
    public Illegal reportForum(Long memberId, Long forumId, IllegalReason reason, String description) {
        log.info("토론 신고 등록 - MemberId: {}, ForumId: {}, Reason: {}", memberId, forumId, reason);

        Illegal illegal = Illegal.reportForum(memberId, forumId, reason, description);
        return illegalRepository.save(illegal);
    }

    // 신고 승인 (관리자)
    @Transactional
    public void approveReport(Long illegalId) {
        Illegal illegal = illegalRepository.findById(illegalId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다: " + illegalId));

        illegal.approve();
        log.info("신고 승인 - IllegalId: {}, MemberId: {}", illegalId, illegal.getMemberId());

        memberService.deactivateMember(illegal.getMemberId());
    }

    // 신고 거부 (관리자)
    @Transactional
    public void rejectReport(Long illegalId) {
        Illegal illegal = illegalRepository.findById(illegalId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다: " + illegalId));

        illegal.reject();
        log.info("신고 거부 - IllegalId: {}, MemberId: {}", illegalId, illegal.getMemberId());
    }

    // REGISTERED 상태를 PENDING으로 자동 변경 (일정 시간 경과 후)
    @Transactional
    public void convertRegisteredToPending(int hoursAfterRegistration) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hoursAfterRegistration);

        List<Illegal> registeredReports = illegalRepository
                .findByIllegalStatusAndCreatedDateBefore(IllegalStatus.REGISTERED, cutoffTime);

        registeredReports.forEach(Illegal::markAsPending);

        log.info("REGISTERED → PENDING 변경 완료 - 변경 건수: {}", registeredReports.size());
    }

    // 특정 회원의 신고 내역 조회
    public List<Illegal> getReportsByMember(Long memberId) {
        return illegalRepository.findByMemberId(memberId);
    }

    // 특정 상태의 신고 내역 조회
    public List<Illegal> getReportsByStatus(IllegalStatus status) {
        return illegalRepository.findByIllegalStatus(status);
    }
}
