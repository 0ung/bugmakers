package com.bugmaker.apt.service.member;

import com.bugmaker.apt.domain.common.Commented;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.dto.mypage.UserMyPageCountResponse;
import com.bugmaker.apt.dto.mypage.UserMyPageListResponse;
import com.bugmaker.apt.enums.member.Status;
import com.bugmaker.apt.repository.common.*;
import com.bugmaker.apt.repository.forum.ForumRepository;
import com.bugmaker.apt.repository.forum.VoteRepository;
import com.bugmaker.apt.repository.myPage.UserMyPageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UserMyPageService - 마이페이지(USER)
 * 내 활동, 통계, 받은 반응 등
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMyPageService {

    private final ForumRepository forumRepository;
    private final LikedRepository likedRepository;
    private final FavoritedRepository favoritedRepository;
    private final UserMyPageRepository userMyPageRepository;
    private final ViewedRepository viewedRepository;
    private final CommentedRepository commentedRepository;
    private final SharedRepository sharedRepository;
    private final ReportedRepository reportedRepository;
    private final VoteRepository voteRepository;

    // ===== 위젯 통계 (개수) =====

    /**
     * 위젯 통계 조회
     */
    public UserMyPageCountResponse getWidgetStats(Member member) {
        log.info("위젯 통계 조회 - MemberId: {}", member.getId());

        Long memberId = member.getId();

        // #1. 자주 보는 것
        Long viewedNews = viewedRepository.countByMember_IdAndNews_IsNotNull(memberId);
        Long viewedForums = viewedRepository.countByMember_IdAndForum_IsNotNull(memberId);
        Long viewedRegions = 10L;

        // #2. 좋아요한 것
        Long myLikeNews = likedRepository.countMyLikedNews(memberId);
        Long myLikeForum = likedRepository.countMyLikedForums(memberId);
        Long myLikeComment = likedRepository.countMyLikedComments(memberId);

        // #3. 즐겨찾기
        Long myFavoriteNews = favoritedRepository.countMyFavoritedNews(memberId);
        Long myFavoriteForum = favoritedRepository.countMyFavoritedForums(memberId);

        // #4. 나의 게시글
        Long myForum = forumRepository.countByMemberAndStatus(member, Status.ACTIVE);
        Long myComment = commentedRepository.countByMember_Id(memberId);

        // #5. 참여중인 토론
        Long myVotedForum = voteRepository.countForumsIVoted(memberId);

        // #6. 공유
        Long myShareNews = sharedRepository.countMySharedNews(memberId);
        Long myShareForum = sharedRepository.countMySharedForums(memberId);

        // #7. 신고함
        Long myReport = reportedRepository.countMyReports(memberId);

        // #8. 받은 좋아요
        Long receivedLikeOnForum = userMyPageRepository.countLikesOnMyForums(memberId);
        Long receivedLikeOnComment = commentedRepository.countLikesOnMyComments(memberId);

        // #9. 받은 댓글
        Long receivedComment = 0L;

        return UserMyPageCountResponse.builder()
                .viewedNewsCount(viewedNews)
                .viewedRegionsCount(viewedRegions)
                .viewedForumsCount(viewedForums)
                .myLikeNewsCount(myLikeNews)
                .myLikeForumCount(myLikeForum)
                .myLikeCommentCount(myLikeComment)
                .myFavoriteNewsCount(myFavoriteNews)
                .myFavoriteForumCount(myFavoriteForum)
                .myForumCount(myForum)
                .myCommentCount(myComment)
                .myVotedForumCount(myVotedForum)
                .myShareNewsCount(myShareNews)
                .myShareForumCount(myShareForum)
                .myReportCount(myReport)
                .receivedLikeOnForumCount(receivedLikeOnForum)
                .receivedLikeOnCommentCount(receivedLikeOnComment)
                .receivedCommentCount(receivedComment)
                .build();
    }

    // ===== 활동별 상세 리스트 (목록) =====

    /**
     * 활동별 상세 리스트 조회
     */
    public Page<UserMyPageListResponse> getActivities(Member member, String type, Pageable pageable) {
        log.info("활동 리스트 조회 - MemberId: {}, Type: {}", member.getId(), type);

        return switch (type) {
            case "viewed_news" -> getViewedNews(member, pageable);
            case "viewed_forums" -> getViewedForums(member, pageable);
            case "my_like_news" -> getMyLikeNews(member, pageable);
            case "my_like_forum" -> getMyLikeForums(member, pageable);
            case "my_like_comment" -> getMyLikeComments(member, pageable);
            case "my_favorite_news" -> getMyFavoriteNews(member, pageable);
            case "my_favorite_forum" -> getMyFavoriteForums(member, pageable);
            case "my_posts" -> getMyPosts(member, pageable);
            case "my_comments" -> getMyComments(member, pageable);
            case "my_voted_forums" -> getMyVotedForums(member, pageable);
            case "my_share_news" -> getMyShareNews(member, pageable);
            case "my_share_forum" -> getMyShareForums(member, pageable);
            case "my_reports" -> getMyReports(member, pageable);
            case "others_like_forum" -> getOthersLikeMyForums(member, pageable);
            case "others_like_comment" -> getOthersLikeMyComments(member, pageable);
            case "others_comments" -> getOthersCommentsOnMyForums(member, pageable);
            default -> Page.empty(pageable);
        };
    }

    /**
     * 즐겨찾기 목록 조회
     */
    public Page<UserMyPageListResponse> getFavorites(Member member, Pageable pageable) {
        log.info("즐겨찾기 목록 조회 - MemberId: {}", member.getId());
        return Page.empty(pageable);
    }

    /**
     * 자주 보는 지역 조회
     */
    public List<UserMyPageListResponse> getViewedRegions(Member member) {
        log.info("자주 보는 지역 조회 - MemberId: {}", member.getId());
        // TODO: Viewed 기반으로 지역별 집계 쿼리
        return new ArrayList<>();
    }

    /**
     * 작성한 게시글 개수
     */
    public long getMyForumsCount(Member member) {
        return forumRepository.countByMemberAndStatus(member, Status.ACTIVE);
    }

    // ===== Private 메서드들 =====

    /** 자주 보는 뉴스 */
    private Page<UserMyPageListResponse> getViewedNews(Member member, Pageable pageable) {
        Page<News> newsPage = viewedRepository.findViewedNews(member.getId(), pageable);
        return newsPage.map(news -> UserMyPageListResponse.forNews(
                news.getId(),
                news.getTitle(),
                truncate(news.getContent(), 100),
                news.getReference(),
                news.getCreatedDate(),
                news.getViewCount(),
                news.getLikeCount()
        ));
    }

    /** 자주 보는 포럼 */
    private Page<UserMyPageListResponse> getViewedForums(Member member, Pageable pageable) {
        Page<Forum> forumPage = viewedRepository.findViewedForums(member.getId(), pageable);
        return forumPage.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 내가 좋아요한 뉴스 */
    private Page<UserMyPageListResponse> getMyLikeNews(Member member, Pageable pageable) {
        Page<News> newsPage = likedRepository.findMyLikedNews(member.getId(), pageable);
        return newsPage.map(news -> UserMyPageListResponse.forNews(
                news.getId(),
                news.getTitle(),
                truncate(news.getContent(), 100),
                news.getReference(),
                news.getCreatedDate(),
                news.getViewCount(),
                news.getLikeCount()
        ));
    }

    /** 내가 좋아요한 포럼 */
    private Page<UserMyPageListResponse> getMyLikeForums(Member member, Pageable pageable) {
        Page<Forum> forumPage = likedRepository.findMyLikedForums(member.getId(), pageable);
        return forumPage.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 내가 좋아요한 댓글 */
    private Page<UserMyPageListResponse> getMyLikeComments(Member member, Pageable pageable) {
        Page<Commented> commentPage = likedRepository.findMyLikedComments(member.getId(), pageable);
        return commentPage.map(comment -> UserMyPageListResponse.forComment(
                comment.getId(),
                "댓글",
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getCreatedDate(),
                comment.getLikeCount()
        ));
    }

    /** 즐겨찾기한 뉴스 */
    private Page<UserMyPageListResponse> getMyFavoriteNews(Member member, Pageable pageable) {
        Page<News> newsPage = favoritedRepository.findMyFavoritedNews(member.getId(), pageable);
        return newsPage.map(news -> UserMyPageListResponse.forNews(
                news.getId(),
                news.getTitle(),
                truncate(news.getContent(), 100),
                news.getReference(),
                news.getCreatedDate(),
                news.getViewCount(),
                news.getLikeCount()
        ));
    }

    /** 즐겨찾기한 포럼 */
    private Page<UserMyPageListResponse> getMyFavoriteForums(Member member, Pageable pageable) {
        Page<Forum> forumPage = favoritedRepository.findMyFavoritedForums(member.getId(), pageable);
        return forumPage.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 내가 작성한 포럼 */
    private Page<UserMyPageListResponse> getMyPosts(Member member, Pageable pageable) {
        Page<Forum> forums = forumRepository.findByMemberAndStatus(member, Status.ACTIVE, pageable);
        return forums.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                member.getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 내가 작성한 댓글 */
    private Page<UserMyPageListResponse> getMyComments(Member member, Pageable pageable) {
        Page<Commented> commentPage = commentedRepository.findByMember_Id(member.getId(), pageable);
        return commentPage.map(comment -> UserMyPageListResponse.forComment(
                comment.getId(),
                "댓글",
                comment.getContent(),
                member.getNickname(),
                comment.getCreatedDate(),
                comment.getLikeCount()
        ));
    }

    /** 투표한 포럼 */
    private Page<UserMyPageListResponse> getMyVotedForums(Member member, Pageable pageable) {
        Page<Forum> forumPage = voteRepository.findForumsIVoted(member.getId(), pageable);
        return forumPage.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 공유한 뉴스 */
    private Page<UserMyPageListResponse> getMyShareNews(Member member, Pageable pageable) {
        Page<News> newsPage = sharedRepository.findMySharedNews(member.getId(), pageable);
        return newsPage.map(news -> UserMyPageListResponse.forNews(
                news.getId(),
                news.getTitle(),
                truncate(news.getContent(), 100),
                news.getReference(),
                news.getCreatedDate(),
                news.getViewCount(),
                news.getLikeCount()
        ));
    }

    /** 공유한 포럼 */
    private Page<UserMyPageListResponse> getMyShareForums(Member member, Pageable pageable) {
        Page<Forum> forumPage = sharedRepository.findMySharedForums(member.getId(), pageable);
        return forumPage.map(forum -> UserMyPageListResponse.forForum(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getCreatedDate(),
                0L,
                0L,
                0L
        ));
    }

    /** 신고한 내역 */
    private Page<UserMyPageListResponse> getMyReports(Member member, Pageable pageable) {
        Page<com.bugmaker.apt.domain.common.Reported> reportPage = 
                reportedRepository.findByReporter_Id(member.getId(), pageable);

        return reportPage.map(report -> {
            String title = "신고";
            String content = report.getReason().name();
            String type = "report";
            Long targetId = null;

            if (report.isForumReport()) {
                title = "포럼 신고";
                content = report.getForum().getTitle();
                type = "forum";
                targetId = report.getForum().getId();
            } else if (report.isCommentReport()) {
                title = "댓글 신고";
                content = report.getCommented().getContent();
                type = "comment";
                targetId = report.getCommented().getId();
            } else if (report.isMemberReport()) {
                title = "사용자 신고";
                content = report.getTargetMember().getNickname();
                type = "member";
                targetId = report.getTargetMember().getId();
            }

            return UserMyPageListResponse.forReport(
                    targetId,
                    type,
                    title,
                    content,
                    report.getReporter().getNickname(),
                    report.getCreatedDate()
            );
        });
    }

    /** 남이 내 포럼에 좋아요한 목록 */
    private Page<UserMyPageListResponse> getOthersLikeMyForums(Member member, Pageable pageable) {
        List<Map<String, Object>> whoLiked = userMyPageRepository.findWhoLikedMyForums(member.getId());
        
        List<UserMyPageListResponse> items = whoLiked.stream()
                .map(map -> UserMyPageListResponse.forForum(
                        ((Number) map.get("forumId")).longValue(),
                        (String) map.get("forumTitle"),
                        "",
                        (String) map.get("nickname"),
                        null,
                        0L,
                        0L,
                        0L
                ))
                .toList();
        
        return new PageImpl<>(items, pageable, items.size());
    }

    /** 남이 내 댓글에 좋아요한 목록 */
    private Page<UserMyPageListResponse> getOthersLikeMyComments(Member member, Pageable pageable) {
        List<Map<String, Object>> whoLiked = userMyPageRepository.findWhoLikedMyComments(member.getId());
        
        List<UserMyPageListResponse> items = whoLiked.stream()
                .map(map -> UserMyPageListResponse.forComment(
                        ((Number) map.get("commentId")).longValue(),
                        "댓글 좋아요",
                        "",
                        (String) map.get("nickname"),
                        null,
                        0L
                ))
                .toList();
        
        return new PageImpl<>(items, pageable, items.size());
    }

    /** 내 글에 달린 댓글 */
    private Page<UserMyPageListResponse> getOthersCommentsOnMyForums(Member member, Pageable pageable) {
        Page<Commented> commentPage = commentedRepository.findCommentsOnMyForums(member.getId(), pageable);
        return commentPage.map(comment -> UserMyPageListResponse.forComment(
                comment.getId(),
                comment.getForum().getTitle(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getCreatedDate(),
                comment.getLikeCount()
        ));
    }

    // ===== 유틸리티 메서드 =====

    /**
     * 텍스트 자르기
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
