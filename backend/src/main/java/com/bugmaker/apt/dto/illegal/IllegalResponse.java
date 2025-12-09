package com.bugmaker.apt.dto.illegal;

import com.bugmaker.apt.constants.IllegalReason;
import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.domain.common.Illegal;

import java.time.LocalDateTime;

public record IllegalResponse(
        Long id,
        Long memberId,
        IllegalReason reason,
        String reasonDisplayName,
        String description,
        Long newsId,
        Long forumId,
        IllegalStatus illegalStatus,
        String illegalStatusDisplayName,
        LocalDateTime createdDate,
        boolean isNewsReport,
        boolean isForumReport
) {
    public static IllegalResponse from(Illegal illegal) {
        return new IllegalResponse(
                illegal.getId(),
                illegal.getMemberId(),
                illegal.getReason(),
                illegal.getReason().getDisplayName(),
                illegal.getDescription(),
                illegal.getNewsId(),
                illegal.getForumId(),
                illegal.getIllegalStatus(),
                illegal.getIllegalStatus().getDisplayName(),
                illegal.getCreatedDate(),
                illegal.isNewsReport(),
                illegal.isForumReport()
        );
    }
}
