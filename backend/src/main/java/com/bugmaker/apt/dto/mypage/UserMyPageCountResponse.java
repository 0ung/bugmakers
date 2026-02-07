package com.bugmaker.apt.dto.mypage;

import lombok.Builder;
import lombok.Getter;

/**
 * 마이페이지 위젯 개수 응답 (16개 위젯)
 * 마이페이지 진입 시 각 위젯의 개수를 한 번에 조회
 */
@Getter
@Builder
public class UserMyPageCountResponse {
    // #1. 자주 보는 것 (조회수 랭킹)
    private final Long viewedNewsCount;
    private final Long viewedRegionsCount;  // TOP 10
    private final Long viewedForumsCount;

    // #2. 좋아요한 것
    private final Long myLikeNewsCount;
    private final Long myLikeForumCount;
    private final Long myLikeCommentCount;

    // #3. 즐겨찾기
    private final Long myFavoriteNewsCount;
    private final Long myFavoriteForumCount;

    // #4. 나의 게시글
    private final Long myForumCount;
    private final Long myCommentCount;

    // #5. 참여중인 토론 (투표)
    private final Long myVotedForumCount;

    // #6. 공유
    private final Long myShareNewsCount;
    private final Long myShareForumCount;

    // #7. 신고함
    private final Long myReportCount;

    // #8. 받은 좋아요
    private final Long receivedLikeOnForumCount;
    private final Long receivedLikeOnCommentCount;

    // #9. 받은 댓글
    private final Long receivedCommentCount;

    /**
     * 빈 통계 객체 생성 (모든 값 0)
     */
    public static UserMyPageCountResponse empty() {
        return UserMyPageCountResponse.builder()
                .viewedNewsCount(0L)
                .viewedRegionsCount(0L)
                .viewedForumsCount(0L)
                .myLikeNewsCount(0L)
                .myLikeForumCount(0L)
                .myLikeCommentCount(0L)
                .myFavoriteNewsCount(0L)
                .myFavoriteForumCount(0L)
                .myForumCount(0L)
                .myCommentCount(0L)
                .myVotedForumCount(0L)
                .myShareNewsCount(0L)
                .myShareForumCount(0L)
                .myReportCount(0L)
                .receivedLikeOnForumCount(0L)
                .receivedLikeOnCommentCount(0L)
                .receivedCommentCount(0L)
                .build();
    }
}
