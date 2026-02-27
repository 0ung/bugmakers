package com.bugmaker.apt.contoller.forum;


import com.bugmaker.apt.common.response.ApiResponse;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumDeleteRequest;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.forum.VoteSubmitRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.common.SliceResponse;
import com.bugmaker.apt.dto.forum.ForumDetailResponse;
import com.bugmaker.apt.dto.forum.ForumListResponse;
import com.bugmaker.apt.service.forum.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/api/forum/{id}")
    public ApiResponse<ForumDetailResponse> findDetail(@PathVariable Long id,
                                                  @AuthenticationPrincipal Member member) {
        ForumDetailResponse detail = forumService.findDetail(id, member);
        return ApiResponse.ok(detail);
    }

    @PostMapping("/api/forum/{forumId}/vote")
    public ApiResponse<ForumDetailResponse> submitVote(@PathVariable Long forumId,
                                                  @RequestBody @Valid VoteSubmitRequest request,
                                                  @AuthenticationPrincipal Member member) {
        ForumDetailResponse detail = forumService.submitVote(forumId, request, member);
        return ApiResponse.ok(detail);
    }

    @PostMapping("/api/forum")
    public ApiResponse<ForumCreateResponse> postUp(@RequestBody @Valid ForumCreateRequest createRequest,
                                                   @AuthenticationPrincipal Member member) {
        Forum forum = forumService.postUp(createRequest, member);
        return ApiResponse.created(ForumCreateResponse.of(forum));
    }

    @PutMapping("/api/forum")
    public ApiResponse<ForumUpdateResponse> update(@RequestBody @Valid ForumUpdateRequest updateRequest,
                                                   @AuthenticationPrincipal Member member) {
        Forum forum = forumService.update(updateRequest, member);
        return ApiResponse.ok(ForumUpdateResponse.of(forum));
    }

    @DeleteMapping("/api/forum")
    public ApiResponse<ForumDeleteResponse> delete(@RequestBody ForumDeleteRequest deleteRequest,
                                                   @AuthenticationPrincipal Member member) {
        Forum forum = forumService.delete(deleteRequest, member);
        return ApiResponse.ok(ForumDeleteResponse.of(forum));
    }

    @GetMapping("/api/forums")
    public ApiResponse<SliceResponse<ForumListResponse>> forumList(
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable) {
        SliceResponse<ForumListResponse> response = forumService.forumList(pageable);
        return ApiResponse.ok(response);
    }
}
