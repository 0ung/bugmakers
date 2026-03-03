package com.bugmaker.apt.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

public record ForumDetailResponse(
        Long forumId,
        String title,
        String content,
        String authorNickname,
        Long viewCount,
        Long likeCount,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        List<VoteResponse> voteList,
        Long myVoteId,
        long totalVoteCount,
        LocalDateTime voteDeadline,
        boolean voteOpen
) {
}
