package com.bugmaker.apt.domain.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 500, message = "댓글은 500자 이내로 작성해주세요.")
        String content,
        Long parentId
) {
}
