import axios from "axios";
import type {
  UserStatsResponse,
  ActivityItem,
  PageResponse,
  TopLikedNewsResponse,
  TopLikedForumResponse,
  TopLikedItemResponse,
  TopLikedCommentResponse,
  TopWriterResponse,
  TopViewedNewsResponse,
  TopViewedForumResponse,
  AdminDashboardResponse,
} from "../types/mypage";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// API 클라이언트 설정
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // 쿠키 포함 (인증)
});

// User API (일반 회원)
/**
 * 위젯 통계 조회
 * GET /api/mypage/widget-stats
 */
export const getUserWidgetStats = async (): Promise<UserStatsResponse> => {
  const response = await apiClient.get<UserStatsResponse>("/api/mypage/widget-stats");
  return response.data;
};

/**
 * 활동별 상세 리스트 조회
 * GET /api/mypage/activities/{type}
 */
export const getUserActivities = async (
  type: string,
  page: number = 0,
  size: number = 20
): Promise<PageResponse<ActivityItem>> => {
  const response = await apiClient.get<PageResponse<ActivityItem>>(
    `/api/mypage/activities/${type}`,
    { params: { page, size } }
  );
  return response.data;
};

// Admin API (관리자)
/**
 * 좋아요 많은 뉴스 TOP N
 * GET /api/admin/mypage/top-liked-news
 */
export const getTopLikedNews = async (limit: number = 10): Promise<TopLikedNewsResponse[]> => {
  const response = await apiClient.get<TopLikedNewsResponse[]>(
    "/api/admin/mypage/top-liked-news",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 좋아요 많은 포럼 TOP N
 * GET /api/admin/mypage/top-liked-forums
 */
export const getTopLikedForums = async (limit: number = 10): Promise<TopLikedForumResponse[]> => {
  const response = await apiClient.get<TopLikedForumResponse[]>(
    "/api/admin/mypage/top-liked-forums",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 좋아요 많은 전체 게시글 TOP N
 * GET /api/admin/mypage/top-liked-all
 */
export const getTopLikedAll = async (limit: number = 10): Promise<TopLikedItemResponse[]> => {
  const response = await apiClient.get<TopLikedItemResponse[]>(
    "/api/admin/mypage/top-liked-all",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 좋아요 많은 댓글 TOP N
 * GET /api/admin/mypage/top-liked-comments
 */
export const getTopLikedComments = async (
  limit: number = 10
): Promise<TopLikedCommentResponse[]> => {
  const response = await apiClient.get<TopLikedCommentResponse[]>(
    "/api/admin/mypage/top-liked-comments",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 포럼 글 많이 쓴 사람 TOP N
 * GET /api/admin/mypage/top-forum-writers
 */
export const getTopForumWriters = async (limit: number = 10): Promise<TopWriterResponse[]> => {
  const response = await apiClient.get<TopWriterResponse[]>(
    "/api/admin/mypage/top-forum-writers",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 댓글 많이 쓴 사람 TOP N
 * GET /api/admin/mypage/top-comment-writers
 */
export const getTopCommentWriters = async (limit: number = 10): Promise<TopWriterResponse[]> => {
  const response = await apiClient.get<TopWriterResponse[]>(
    "/api/admin/mypage/top-comment-writers",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 조회수 많은 뉴스 TOP N
 * GET /api/admin/mypage/top-viewed-news
 */
export const getTopViewedNews = async (limit: number = 10): Promise<TopViewedNewsResponse[]> => {
  const response = await apiClient.get<TopViewedNewsResponse[]>(
    "/api/admin/mypage/top-viewed-news",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 조회수 많은 포럼 TOP N
 * GET /api/admin/mypage/top-viewed-forums
 */
export const getTopViewedForums = async (
  limit: number = 10
): Promise<TopViewedForumResponse[]> => {
  const response = await apiClient.get<TopViewedForumResponse[]>(
    "/api/admin/mypage/top-viewed-forums",
    { params: { limit } }
  );
  return response.data;
};

/**
 * 관리자 대시보드 통합 통계
 * GET /api/admin/mypage/dashboard
 */
export const getAdminDashboard = async (): Promise<AdminDashboardResponse> => {
  const response = await apiClient.get<AdminDashboardResponse>("/api/admin/mypage/dashboard");
  return response.data;
};
