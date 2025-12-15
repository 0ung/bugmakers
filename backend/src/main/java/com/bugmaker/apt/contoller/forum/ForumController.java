package com.bugmaker.apt.contoller.forum;


import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.service.forum.ForumModifyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForumController {
    private final ForumModifyService forumModifyService;

    @PostMapping("/api/forum")
    public ForumCreateResponse postUp(@RequestBody @Valid ForumCreateRequest createRequest,
                                      @AuthenticationPrincipal Member member) {
        Forum forum = forumModifyService.postUp(createRequest, member);

        return ForumCreateResponse.of(forum);
    }

    @PutMapping("/api/forum")
    public ForumUpdateResponse update(@RequestBody @Valid ForumUpdateRequest updateRequest) {
        Forum forum = forumModifyService.update(updateRequest);

        return ForumUpdateResponse.of(forum);
    }




}
