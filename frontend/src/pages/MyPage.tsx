import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";
import MainLayout from "../components/layouts/MainLayout";
import UserMyPage from "./mypage/UserMyPage";
import AdminMyPage from "./mypage/AdminMyPage";

export default function MyPage() {
  const navigate = useNavigate();
  const { isLoggedIn, user, isInitialized } = useAuthStore();

  // 로그인 체크
  useEffect(() => {
    if (isInitialized && !isLoggedIn) {
      alert("로그인이 필요합니다.");
      navigate("/login");
    }
  }, [isInitialized, isLoggedIn, navigate]);

  // 로딩 중
  if (!isInitialized || !user) {
    return (
      <MainLayout>
        <div className="max-w-7xl mx-auto py-20 text-center text-gray-500">
          로딩 중...
        </div>
      </MainLayout>
    );
  }

  // ROLE에 따라 다른 컴포넌트 렌더링
  return user.memberRole === "ADMIN" ? <AdminMyPage /> : <UserMyPage />;
}
