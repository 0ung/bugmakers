/**
 * 마이페이지 API 타입 정의
 */

// User (일반 회원)
/** 위젯 통계 응답 */
export interface UserStatsResponse {
  viewNewsCount: number;
  viewTrendCount: number;
  viewForumCount: number;
  myLikeNewsCount: number;
  myLikeForumCount: number;
  othersLikeNewsCount: number;
  othersLikeForumCount: number;
  myFavoriteCount: number;
  othersFavoriteCount: number;
  myShareCount: number;
  othersShareCount: number;
  myReportCount: number;
  othersReportCount: number;
}

/** 활동 아이템 응답 */
export interface ActivityItem {
  id: number;
  type: string; // "news", "forum", "comment"
  title: string;
  content: string;
  author: string;
  createdAt: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
}

/** 페이지 응답 */
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  empty: boolean;
}

// Admin (관리자)
/** 좋아요 많은 뉴스 응답 */
export interface TopLikedNewsResponse {
  newsId: number;
  title: string;
  author: string;
  likeCount: number;
}

/** 좋아요 많은 포럼 응답 */
export interface TopLikedForumResponse {
  forumId: number;
  title: string;
  memberId: number;
  nickname: string;
  likeCount: number;
}

/** 좋아요 많은 전체 게시글 응답 */
export interface TopLikedItemResponse {
  type: string; // "news" or "forum"
  id: number;
  title: string;
  author: string;
  likeCount: number;
}

/** 좋아요 많은 댓글 응답 */
export interface TopLikedCommentResponse {
  commentId: number;
  memberId: number;
  nickname: string;
  likeCount: number;
}

/** 작성자 순위 응답 */
export interface TopWriterResponse {
  memberId: number;
  nickname: string;
  count: number;
}

/** 조회수 많은 뉴스 응답 */
export interface TopViewedNewsResponse {
  newsId: number;
  title: string;
  author: string;
  viewCount: number;
}

/** 조회수 많은 포럼 응답 */
export interface TopViewedForumResponse {
  forumId: number;
  title: string;
  nickname: string;
  viewCount: number;
}

/** 관리자 대시보드 응답 */
export interface AdminDashboardResponse {
  topLikedNews: TopLikedNewsResponse[];
  topLikedForums: TopLikedForumResponse[];
  topViewedNews: TopViewedNewsResponse[];
  topViewedForums: TopViewedForumResponse[];
  topForumWriters: TopWriterResponse[];
  topCommentWriters: TopWriterResponse[];
}
