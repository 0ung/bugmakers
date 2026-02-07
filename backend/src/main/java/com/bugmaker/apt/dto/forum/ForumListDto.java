package com.bugmaker.apt.dto.forum;

import java.time.LocalDateTime;

public record ForumListDto(
        Long forumId,
        String title,
        String content,
        String authorNickname,
        Long viewCount,
        Long likeCount,
        Long reportCount,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
