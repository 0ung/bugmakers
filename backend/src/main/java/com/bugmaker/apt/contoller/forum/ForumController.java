package com.bugmaker.apt.contoller.forum;


import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumDeleteRequest;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.service.forum.ForumModifyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForumController {
    private final ForumModifyService forumModifyService;

    @PostMapping("/api/forum")
    public ResponseEntity<ForumCreateResponse> postUp(@RequestBody @Valid ForumCreateRequest createRequest,
                                                     @AuthenticationPrincipal Member member) {
        Forum forum = forumModifyService.postUp(createRequest, member);

        return new ResponseEntity<>(ForumCreateResponse.of(forum), CREATED);
    }

    @PutMapping("/api/forum")
    public ResponseEntity<ForumUpdateResponse> update(@RequestBody @Valid ForumUpdateRequest updateRequest,
                                      @AuthenticationPrincipal Member member) {
        Forum forum = forumModifyService.update(updateRequest, member);

        return new ResponseEntity<>(ForumUpdateResponse.of(forum), HttpStatus.OK);
    }

    @DeleteMapping("/api/forum")
    public ResponseEntity<ForumDeleteResponse> delete(@RequestBody ForumDeleteRequest deleteRequest,
                                                      @AuthenticationPrincipal Member member) {
        // 필드값 ForumId 외 추가될 일 없으면 PathVariable로 받을지 고민
        Forum forum = forumModifyService.delete(deleteRequest, member);

        return new ResponseEntity<>(ForumDeleteResponse.of(forum), HttpStatus.OK);
    }
}
