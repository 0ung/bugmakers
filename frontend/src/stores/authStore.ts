// src/stores/authStore.ts
import { create } from "zustand";
import { api } from "../utils/axios";
import type { Member } from "../types/member";

// interface User {
//   id: number;
//   createdDate: string;
//   lastModifiedDate: string;
//
//   email?: {
//     address: string;
//   };
//
//   nickname: string;
//   memberRole: "USER" | "ADMIN";
//   status: "ACTIVE" | "INACTIVE";
//
//   provider: "kakao" | "google" | "local";
//   providerId: string;
//
//   active: boolean;
//   enabled: boolean;
//   username: string;
//
//   authorities: {
//     authority: string;
//   }[];
//
//   profileImageUrl?: string;
//   // 필요한 필드 추가
// }

interface AuthState {
  isLoggedIn: boolean;
  user: Member | null;
  isLoading: boolean; // /auth/me 호출 중인지
  isInitialized: boolean; // 앱이 처음 유저 정보 로딩 완료했는지

  loadUser: () => Promise<void>;
  logout: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  isLoggedIn: false,
  user: null,
  isLoading: false,
  isInitialized: false,

  // 앱 시작 시 또는 새로고침 시 호출
  loadUser: async () => {
    set({ isLoading: true });
    try {
      const res = await api.get<Member>("/member/about/me");
      set({
        isLoggedIn: true,
        user: res.data,
        isLoading: false,
        isInitialized: true,
      });
    } catch (e) {
      // 401 등 → 비로그인 상태
      set({
        isLoggedIn: false,
        user: null,
        isLoading: false,
        isInitialized: true,
      });
    }
  },

  logout: async () => {
    try {
      await api.post("/auth/logout");
    } catch (e) {
      // 로그아웃 API 실패해도 프론트 상태는 그냥 비움
    }
    set({
      isLoggedIn: false,
      user: null,
    });
  },
}));
