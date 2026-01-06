package com.bugmaker.apt.contoller.member;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MyPageForumResponse;
import com.bugmaker.apt.domain.member.MyPageProfileResponse;
import com.bugmaker.apt.domain.member.MyPageProfileUpdateRequest;
import com.bugmaker.apt.service.member.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 프로필 정보 조회
     * GET /api/mypage/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<MyPageProfileResponse> getProfile(
            @AuthenticationPrincipal Member member) {
        
        MyPageProfileResponse response = myPageService.getProfile(member);
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 정보 수정
     * PUT /api/mypage/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<MyPageProfileResponse> updateProfile(
            @AuthenticationPrincipal Member member,
            @RequestBody @Valid MyPageProfileUpdateRequest updateRequest) {
        
        MyPageProfileResponse response = myPageService.updateProfile(member, updateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 작성한 게시글 조회
     * GET /api/mypage/forums
     * 
     * @param pageable 페이징 정보 (page, size, sort)
     * 예: /api/mypage/forums?page=0&size=10
     */
    @GetMapping("/forums")
    public ResponseEntity<Page<MyPageForumResponse>> getMyForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<MyPageForumResponse> response = myPageService.getMyForums(member, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 활동 통계 조회
     * GET /api/mypage/stats
     * 
     * @return 게시글 수, 댓글 수 등의 통계
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getActivityStats(
            @AuthenticationPrincipal Member member) {
        
        long forumCount = myPageService.getMyForumsCount(member);
        
        Map<String, Long> stats = Map.of(
                "forumCount", forumCount
                // TODO: 댓글, 좋아요 등 추가 통계
        );
        
        return ResponseEntity.ok(stats);
    }
}
