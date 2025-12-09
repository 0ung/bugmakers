package com.bugmaker.apt.contoller.illegal;

import com.bugmaker.apt.dto.illegal.IllegalResponse;
import com.bugmaker.apt.service.illegal.IllegalAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 관리자용 신고 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/illegals")
@RequiredArgsConstructor
public class IllegalAdminController {

    private final IllegalAdminService illegalAdminService;

    /**
     * 대기 중인 신고 목록 조회 (REGISTERED + PENDING)
     */
    @GetMapping("/pending")
    public ResponseEntity<List<IllegalResponse>> getPendingReports(
            @RequestHeader("X-Member-Id") Long adminId) {

        List<IllegalResponse> reports = illegalAdminService.getPendingReports(adminId)
                .stream()
                .map(IllegalResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    /**
     * REGISTERED 상태 신고만 조회
     */
    @GetMapping("/registered")
    public ResponseEntity<List<IllegalResponse>> getRegisteredReports(
            @RequestHeader("X-Member-Id") Long adminId) {

        List<IllegalResponse> reports = illegalAdminService.getRegisteredReports(adminId)
                .stream()
                .map(IllegalResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    /**
     * PENDING 상태 신고만 조회
     */
    @GetMapping("/pending-status")
    public ResponseEntity<List<IllegalResponse>> getPendingStatusReports(
            @RequestHeader("X-Member-Id") Long adminId) {

        List<IllegalResponse> reports = illegalAdminService.getPendingStatusReports(adminId)
                .stream()
                .map(IllegalResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    /**
     * 승인된 신고 목록 조회
     */
    @GetMapping("/approved")
    public ResponseEntity<List<IllegalResponse>> getApprovedReports(
            @RequestHeader("X-Member-Id") Long adminId) {

        List<IllegalResponse> reports = illegalAdminService.getApprovedReports(adminId)
                .stream()
                .map(IllegalResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    /**
     * 거부된 신고 목록 조회
     */
    @GetMapping("/rejected")
    public ResponseEntity<List<IllegalResponse>> getRejectedReports(
            @RequestHeader("X-Member-Id") Long adminId) {

        List<IllegalResponse> reports = illegalAdminService.getRejectedReports(adminId)
                .stream()
                .map(IllegalResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reports);
    }

    /**
     * 신고 상세 조회
     */
    @GetMapping("/{illegalId}")
    public ResponseEntity<IllegalResponse> getReportDetail(
            @RequestHeader("X-Member-Id") Long adminId,
            @PathVariable Long illegalId) {

        IllegalResponse response = IllegalResponse.from(
                illegalAdminService.getReportDetail(adminId, illegalId)
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 신고 승인 → 회원 자동 정지
     */
    @PostMapping("/{illegalId}/approve")
    public ResponseEntity<Void> approveReport(
            @RequestHeader("X-Member-Id") Long adminId,
            @PathVariable Long illegalId) {

        illegalAdminService.approveReport(adminId, illegalId);
        return ResponseEntity.ok().build();
    }

    /**
     * 신고 거부
     */
    @PostMapping("/{illegalId}/reject")
    public ResponseEntity<Void> rejectReport(
            @RequestHeader("X-Member-Id") Long adminId,
            @PathVariable Long illegalId) {

        illegalAdminService.rejectReport(adminId, illegalId);
        return ResponseEntity.ok().build();
    }

    /**
     * 신고 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<IllegalAdminService.IllegalStatistics> getStatistics(
            @RequestHeader("X-Member-Id") Long adminId) {

        return ResponseEntity.ok(illegalAdminService.getStatistics(adminId));
    }
}
