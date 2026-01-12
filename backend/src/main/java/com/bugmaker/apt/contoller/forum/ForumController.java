package com.bugmaker.apt.contoller.forum;


import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumDeleteRequest;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.common.SliceResponse;
import com.bugmaker.apt.dto.forum.ForumListDto;
import com.bugmaker.apt.service.forum.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @PostMapping("/api/forum")
    public ResponseEntity<ForumCreateResponse> postUp(@RequestBody @Valid ForumCreateRequest createRequest,
                                                     @AuthenticationPrincipal Member member) {
        Forum forum = forumService.postUp(createRequest, member);

        return new ResponseEntity<>(ForumCreateResponse.of(forum), CREATED);
    }

    @PutMapping("/api/forum")
    public ResponseEntity<ForumUpdateResponse> update(@RequestBody @Valid ForumUpdateRequest updateRequest,
                                      @AuthenticationPrincipal Member member) {
        Forum forum = forumService.update(updateRequest, member);

        return new ResponseEntity<>(ForumUpdateResponse.of(forum), HttpStatus.OK);
    }

    @DeleteMapping("/api/forum")
    public ResponseEntity<ForumDeleteResponse> delete(@RequestBody ForumDeleteRequest deleteRequest,
                                                      @AuthenticationPrincipal Member member) {
        // 필드값 ForumId 외 추가될 일 없으면 PathVariable로 받을지 고민
        Forum forum = forumService.delete(deleteRequest, member);

        return new ResponseEntity<>(ForumDeleteResponse.of(forum), HttpStatus.OK);
    }

    @GetMapping("/api/forums")
    public ResponseEntity<SliceResponse<ForumListDto>> forumList(
            @PageableDefault(size = 10, sort = "createdDate", direction = DESC) Pageable pageable)
    {
        SliceResponse<ForumListDto> response = forumService.forumList(pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
