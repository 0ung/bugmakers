package com.bugmaker.apt.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

public record ForumListResponse(
        Long forumId,
        String title,
        String content,
        String authorNickname,
        Long viewCount,
        Long likeCount,
        Long reportCount,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        List<VoteSummaryResponse> topVotes,
        long totalVoteCount
) {
}
