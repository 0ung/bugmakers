package com.bugmaker.apt.service.illegal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 신고 상태 자동 변경 스케줄러
 * REGISTERED → PENDING (등록 후 24시간 경과 시)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IllegalScheduler {

    private final IllegalService illegalService;

    /**
     * 매일 새벽 3시에 실행
     * 등록 후 24시간 지난 신고를 PENDING으로 변경
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void convertRegisteredToPending() {
        log.info("신고 상태 자동 변경 스케줄러 시작");

        // 24시간 지난 REGISTERED → PENDING 변경
        illegalService.convertRegisteredToPending(24);

        log.info("신고 상태 자동 변경 스케줄러 종료");
    }
}
