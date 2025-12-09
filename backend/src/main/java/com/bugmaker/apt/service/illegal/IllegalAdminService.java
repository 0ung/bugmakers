package com.bugmaker.apt.service.illegal;

import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.domain.common.Illegal;
import com.bugmaker.apt.repository.IllegalRepository;
import com.bugmaker.apt.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 관리자용 신고 관리 서비스 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllegalAdminService {
    private final IllegalRepository illegalRepository;
    private final IllegalService illegalService;
    private final MemberService memberService;

    /**
     * 대기 중인 신고 목록 조회 (REGISTERED + PENDING)
     * 관리자 전용
     */
    public List<Illegal> getPendingReports(Long adminId) {
        // 관리자 권한 확인
        memberService.validateAdmin(adminId);

        List<Illegal> registered = illegalRepository.findByIllegalStatus(IllegalStatus.REGISTERED);
        List<Illegal> pending = illegalRepository.findByIllegalStatus(IllegalStatus.PENDING);

        // 두 목록 합치기
        List<Illegal> allPending = Stream.concat(registered.stream(), pending.stream())
                .collect(Collectors.toList());

        log.info("대기 중인 신고 조회 - AdminId: {}, 총 {}건 (REGISTERED: {}건, PENDING: {}건)",
                adminId, allPending.size(), registered.size(), pending.size());

        return allPending;
    }

    /**
     * REGISTERED 상태 신고 목록 조회
     * 관리자 전용
     */
    public List<Illegal> getRegisteredReports(Long adminId) {
        memberService.validateAdmin(adminId);

        List<Illegal> reports = illegalRepository.findByIllegalStatus(IllegalStatus.REGISTERED);
        log.info("REGISTERED 신고 조회 - AdminId: {}, 총 {}건", adminId, reports.size());

        return reports;
    }

    /**
     * PENDING 상태 신고 목록 조회
     * 관리자 전용
     */
    public List<Illegal> getPendingStatusReports(Long adminId) {
        memberService.validateAdmin(adminId);

        List<Illegal> reports = illegalRepository.findByIllegalStatus(IllegalStatus.PENDING);
        log.info("PENDING 신고 조회 - AdminId: {}, 총 {}건", adminId, reports.size());

        return reports;
    }

    /**
     * 승인된 신고 목록 조회
     * 관리자 전용
     */
    public List<Illegal> getApprovedReports(Long adminId) {
        memberService.validateAdmin(adminId);

        List<Illegal> reports = illegalRepository.findByIllegalStatus(IllegalStatus.APPROVED);
        log.info("APPROVED 신고 조회 - AdminId: {}, 총 {}건", adminId, reports.size());

        return reports;
    }

    /**
     * 거부된 신고 목록 조회
     * 관리자 전용
     */
    public List<Illegal> getRejectedReports(Long adminId) {
        memberService.validateAdmin(adminId);

        List<Illegal> reports = illegalRepository.findByIllegalStatus(IllegalStatus.REJECTED);
        log.info("REJECTED 신고 조회 - AdminId: {}, 총 {}건", adminId, reports.size());

        return reports;
    }

    /**
     * 신고 상세 조회
     * 관리자 전용
     */
    public Illegal getReportDetail(Long adminId, Long illegalId) {
        memberService.validateAdmin(adminId);

        return illegalRepository.findById(illegalId)
                .orElseThrow(() -> new IllegalArgumentException("신고 내역을 찾을 수 없습니다: " + illegalId));
    }

    /**
     * 신고 승인 (관리자) → 회원 자동 정지
     * 관리자 전용
     */
    @Transactional
    public void approveReport(Long adminId, Long illegalId) {
        // 관리자 권한 확인
        memberService.validateAdmin(adminId);

        log.info("신고 승인 요청 - AdminId: {}, IllegalId: {}", adminId, illegalId);

        // IllegalService의 기존 메서드 호출
        illegalService.approveReport(illegalId);
    }

    /**
     * 신고 거부 (관리자)
     * 관리자 전용
     */
    @Transactional
    public void rejectReport(Long adminId, Long illegalId) {
        // 관리자 권한 확인
        memberService.validateAdmin(adminId);

        log.info("신고 거부 요청 - AdminId: {}, IllegalId: {}", adminId, illegalId);

        // IllegalService의 기존 메서드 호출
        illegalService.rejectReport(illegalId);
    }

    /**
     * 신고 상태별 통계 조회
     * 관리자 전용
     */
    public IllegalStatistics getStatistics(Long adminId) {
        memberService.validateAdmin(adminId);

        long registeredCount = illegalRepository.findByIllegalStatus(IllegalStatus.REGISTERED).size();
        long pendingCount = illegalRepository.findByIllegalStatus(IllegalStatus.PENDING).size();
        long approvedCount = illegalRepository.findByIllegalStatus(IllegalStatus.APPROVED).size();
        long rejectedCount = illegalRepository.findByIllegalStatus(IllegalStatus.REJECTED).size();

        IllegalStatistics stats = new IllegalStatistics(
                registeredCount,
                pendingCount,
                approvedCount,
                rejectedCount,
                registeredCount + pendingCount  // 처리 대기 중
        );

        log.info("신고 통계 조회 - AdminId: {}, Stats: {}", adminId, stats);

        return stats;
    }

    /**
     * 신고 통계 DTO
     */
    public record IllegalStatistics(
            long registered,
            long pending,
            long approved,
            long rejected,
            long totalPending  // registered + pending
    ) {}
}
