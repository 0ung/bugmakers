package com.bugmaker.apt.contoller.forum;

import com.bugmaker.apt.common.response.ApiResponse;
import com.bugmaker.apt.domain.forum.CommentCreateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.forum.CommentResponse;
import com.bugmaker.apt.service.forum.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/forum/{forumId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long forumId,
            @AuthenticationPrincipal Member member) {
        return ApiResponse.ok(commentService.getComments(forumId, member));
    }

    @PostMapping("/api/forum/{forumId}/comment")
    public ApiResponse<CommentResponse> createComment(
            @PathVariable Long forumId,
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal Member member) {
        return ApiResponse.created(commentService.create(forumId, request, member));
    }

    @PostMapping("/api/comment/{commentId}/like")
    public ApiResponse<CommentResponse> toggleLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Member member) {
        return ApiResponse.ok(commentService.toggleLike(commentId, member));
    }
}
