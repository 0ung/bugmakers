package com.bugmaker.apt.contoller.member;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.mypage.UserMyPageCountResponse;
import com.bugmaker.apt.dto.mypage.UserMyPageListResponse;
import com.bugmaker.apt.service.member.UserMyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserMyPageController - 마이페이지(USER) API
 * 
 * 내 활동, 통계, 받은 반응 등
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class UserMyPageController {

    private final UserMyPageService userMyPageService;

    // ===== 위젯 통계 (개수) =====

    /**
     * 위젯 통계 조회 (16개 위젯)
     * GET /api/mypage/widget-stats
     */
    @GetMapping("/widget-stats")
    public ResponseEntity<UserMyPageCountResponse> getWidgetStats(
            @AuthenticationPrincipal Member member) {
        
        UserMyPageCountResponse stats = userMyPageService.getWidgetStats(member);
        return ResponseEntity.ok(stats);
    }

    // ===== 활동별 상세 리스트 (목록) =====

    /**
     * 활동별 상세 리스트 조회
     * GET /api/mypage/activities/{type}
     */
    @GetMapping("/activities/{type}")
    public ResponseEntity<Page<UserMyPageListResponse>> getActivities(
            @AuthenticationPrincipal Member member,
            @PathVariable String type,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, type, pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 즐겨찾기 목록 조회 (통합 - deprecated)
     * GET /api/mypage/favorites
     */
    @GetMapping("/favorites")
    public ResponseEntity<Page<UserMyPageListResponse>> getFavorites(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<UserMyPageListResponse> favorites = userMyPageService.getFavorites(member, pageable);
        return ResponseEntity.ok(favorites);
    }

    /**
     * 자주 보는 지역 조회
     * GET /api/mypage/regions
     */
    @GetMapping("/regions")
    public ResponseEntity<List<UserMyPageListResponse>> getViewedRegions(
            @AuthenticationPrincipal Member member) {
        
        List<UserMyPageListResponse> regions = userMyPageService.getViewedRegions(member);
        return ResponseEntity.ok(regions);
    }

    /**
     * 활동 통계 조회 (기존 호환성 유지)
     * GET /api/mypage/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getActivityStats(
            @AuthenticationPrincipal Member member) {
        
        long forumCount = userMyPageService.getMyForumsCount(member);
        
        Map<String, Long> stats = Map.of(
                "forumCount", forumCount
        );
        
        return ResponseEntity.ok(stats);
    }

    // ===== #1. 자주 보는 것 =====

    /**
     * 자주 보는 뉴스
     * GET /api/mypage/viewed/news
     */
    @GetMapping("/viewed/news")
    public ResponseEntity<Page<UserMyPageListResponse>> getViewedNews(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "viewed_news", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 자주 보는 포럼
     * GET /api/mypage/viewed/forums
     */
    @GetMapping("/viewed/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getViewedForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "viewed_forums", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #2. 좋아요한 것 =====

    /**
     * 좋아요한 뉴스
     * GET /api/mypage/liked/news
     */
    @GetMapping("/liked/news")
    public ResponseEntity<Page<UserMyPageListResponse>> getLikedNews(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_like_news", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 좋아요한 포럼
     * GET /api/mypage/liked/forums
     */
    @GetMapping("/liked/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getLikedForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_like_forum", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 좋아요한 댓글
     * GET /api/mypage/liked/comments
     */
    @GetMapping("/liked/comments")
    public ResponseEntity<Page<UserMyPageListResponse>> getLikedComments(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_like_comment", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #3. 즐겨찾기 =====

    /**
     * 즐겨찾기한 뉴스
     * GET /api/mypage/favorites/news
     */
    @GetMapping("/favorites/news")
    public ResponseEntity<Page<UserMyPageListResponse>> getFavoriteNews(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_favorite_news", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 즐겨찾기한 포럼
     * GET /api/mypage/favorites/forums
     */
    @GetMapping("/favorites/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getFavoriteForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_favorite_forum", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #4. 나의 게시글/댓글 =====

    /**
     * 작성한 포럼 글
     * GET /api/mypage/my/forums
     */
    @GetMapping("/my/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getMyForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_posts", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 작성한 댓글
     * GET /api/mypage/my/comments
     */
    @GetMapping("/my/comments")
    public ResponseEntity<Page<UserMyPageListResponse>> getMyComments(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_comments", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #5. 참여중인 토론 =====

    /**
     * 투표한 포럼
     * GET /api/mypage/voted/forums
     */
    @GetMapping("/voted/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getVotedForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_voted_forums", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #6. 공유 =====

    /**
     * 공유한 뉴스
     * GET /api/mypage/shared/news
     */
    @GetMapping("/shared/news")
    public ResponseEntity<Page<UserMyPageListResponse>> getSharedNews(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_share_news", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 공유한 포럼
     * GET /api/mypage/shared/forums
     */
    @GetMapping("/shared/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getSharedForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_share_forum", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #7. 신고함 =====

    /**
     * 신고한 내역
     * GET /api/mypage/reported/all
     */
    @GetMapping("/reported/all")
    public ResponseEntity<Page<UserMyPageListResponse>> getReportedAll(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "my_reports", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #8. 받은 좋아요 =====

    /**
     * 내 포럼 글에 받은 좋아요
     * GET /api/mypage/received/likes/forums
     */
    @GetMapping("/received/likes/forums")
    public ResponseEntity<Page<UserMyPageListResponse>> getReceivedLikesOnForums(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "others_like_forum", pageable);
        return ResponseEntity.ok(activities);
    }

    /**
     * 내 댓글에 받은 좋아요
     * GET /api/mypage/received/likes/comments
     */
    @GetMapping("/received/likes/comments")
    public ResponseEntity<Page<UserMyPageListResponse>> getReceivedLikesOnComments(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "others_like_comment", pageable);
        return ResponseEntity.ok(activities);
    }

    // ===== #9. 받은 댓글 =====

    /**
     * 내 글에 달린 댓글
     * GET /api/mypage/received/comments
     */
    @GetMapping("/received/comments")
    public ResponseEntity<Page<UserMyPageListResponse>> getReceivedComments(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<UserMyPageListResponse> activities = userMyPageService.getActivities(member, "others_comments", pageable);
        return ResponseEntity.ok(activities);
    }
}
