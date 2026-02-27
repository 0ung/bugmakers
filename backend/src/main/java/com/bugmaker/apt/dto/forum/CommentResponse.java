package com.bugmaker.apt.dto.forum;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
        Long commentId,
        Long parentId,
        String content,
        String authorNickname,
        long likeCount,
        boolean likedByMe,
        LocalDateTime createdDate,
        List<CommentResponse> replies
) {
}
