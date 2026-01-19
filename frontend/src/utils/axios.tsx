import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { ApiError } from "../types/error";

// 1. 커스텀 설정 타입 정의 (_retry 속성 포함)
interface CustomRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080",
  withCredentials: true, // 핵심: 모든 요청에 쿠키를 자동으로 포함함
});

let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) prom.reject(error);
    else prom.resolve(token);
  });
  failedQueue = [];
};

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as CustomRequestConfig;

    if (!originalRequest) return Promise.reject(ApiError.from(error));

    // 401 Unauthorized: Access Token 만료 상황
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(() => api(originalRequest))
          .catch((err) => Promise.reject(ApiError.from(err)));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        /**
         * 2. 쿠키 기반 Refresh 로직
         * - 백엔드의 /refresh 엔드포인트는 요청에 담긴 Refresh Token 쿠키를 검증하고
         * - 새로운 Access Token 쿠키를 Set-Cookie 헤더로 내려줍니다.
         * - 프론트엔드에서 토큰을 추출하거나 저장할 필요가 없습니다.
         */
        await api.post("/api/v1/auth/refresh");

        isRefreshing = false;
        processQueue(null);

        // 새로운 쿠키가 설정된 상태로 원래 요청 재시도
        return api(originalRequest);
      } catch (refreshError) {
        isRefreshing = false;
        processQueue(refreshError, null);

        // Refresh Token까지 만료된 경우 -> 완전한 로그아웃 처리
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = "/login";
        return Promise.reject(ApiError.from(refreshError));
      }
    }

    // 403 Forbidden: 접근 권한 없음
    if (error.response?.status === 403) {
      const apiError = ApiError.from(error);
      alert(apiError.message);
      window.location.href = "/login";
      return Promise.reject(apiError);
    }

    // 모든 에러를 ApiError로 변환
    return Promise.reject(ApiError.from(error));
  }
);
