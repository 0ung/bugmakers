package com.bugmaker.apt.repository.myPage;

import java.util.List;
import java.util.Map;

/**
 * UserMyPageRepository - "남이 내 글/댓글에 무엇을 했는가" (소유 기준)
 * 
 * 마이페이지(USER): 내 글/댓글이 받은 좋아요, 즐겨찾기, 공유 등
 * 읽기 전용 Repository (집계/통계용)
 */
public interface UserMyPageRepository {

    // 좋아요 관련
    /**
     * 1. 남이 나의 forum 게시글(목적)에 좋아요를 누른 count
     * 
     * @param memberId 내 회원 ID
     * @return 내 forum 게시글들이 받은 총 좋아요 수
     */
    Long countLikesOnMyForums(Long memberId);

    /**
     * 2. 어떤 사람들(list)이 나의 forum 게시글에 좋아요를 눌렀나?
     * 
     * @param memberId 내 회원 ID
     * @return List<Map> - reportedId, nickname, forumId, forumTitle, likedAt
     */
    List<Map<String, Object>> findWhoLikedMyForums(Long memberId);

    /**
     * 3. 남이 나의 comment에 좋아요를 누른 count
     * 
     * @param memberId 내 회원 ID
     * @return 내 댓글들이 받은 총 좋아요 수
     */
    Long countLikesOnMyComments(Long memberId);

    /**
     * 4. 어떤 사람들(list)이 나의 comment에 좋아요를 눌렀나?
     * 
     * @param memberId 내 회원 ID
     * @return List<Map> - reportedId, nickname, commentId, likedAt
     */
    List<Map<String, Object>> findWhoLikedMyComments(Long memberId);
}
