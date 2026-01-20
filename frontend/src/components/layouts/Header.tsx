// src/components/layout/Header.tsx
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";
import logo from "@/assets/images/myHome.png";
import { useEffect } from "react";

export default function Header() {
  const location = useLocation();
  const navigate = useNavigate();

  const { isLoggedIn, user, loadUser, logout, isInitialized } = useAuthStore();

  // 앱 최초 마운트 때 유저 데이터 로드 (쿠키 기반 인증 체크)
  useEffect(() => {
    if (!isInitialized) loadUser();
  }, [isInitialized, loadUser]);

  // user 변경 감지용 (로그 전용)
  // useEffect(() => {
  //   if (user) {
  //     console.log("user(json):", JSON.stringify(user, null, 2));
  //   }
  // }, [user]);

  const navList = [
    { name: "부동산뉴스", path: "/news" },
    { name: "시세트렌드", path: "/trend" },
    { name: "커뮤니티", path: "/community" },
  ];

  return (
    <header className="sticky top-0 bg-white shadow z-50">
      <div className="max-w-7xl mx-auto px-6 py-5 flex flex-col items-center text-center relative">
        {/* 로고 */}
        <Link to="/" className="block mb-1">
          <img
            src={logo}
            alt="마이홈 로고"
            className="h-20 sm:h-24 md:h-28 object-contain mx-auto"
          />
        </Link>

        {/* 내비게이션 */}
        <nav className="flex space-x-8 border-t border-gray-200 pt-4">
          {navList.map((nav) => {
            const isActive = location.pathname.startsWith(nav.path);
            return (
              <Link
                key={nav.path}
                to={nav.path}
                className={
                  isActive
                    ? "font-semibold text-gray-800 border-b-2 border-blue-600 pb-1"
                    : "text-gray-600 hover:text-gray-900 hover:border-b-2 hover:border-blue-600 pb-1 transition"
                }
              >
                {nav.name}
              </Link>
            );
          })}
        </nav>

        {/* 우측 유저 영역 */}
        <div className="absolute right-8 top-6 flex gap-2 items-center text-sm">
          {!isInitialized ? (
            // 아직 인증 상태 불러오는 중
            <span className="text-gray-500">...</span>
          ) : isLoggedIn && user ? (
            <>
              <button
                onClick={() => navigate("/mypage")}
                className="text-blue-600 border-2 border-blue-600 px-3 py-1.5 rounded-md hover:bg-blue-50 transition font-medium"
              >
                {user.nickname}님
              </button>
              <button
                onClick={async () => {
                  await logout();
                  navigate("/login");
                }}
                className="text-gray-600 border px-3 py-1.5 rounded-md hover:bg-gray-100 transition"
              >
                로그아웃
              </button>
            </>
          ) : (
            <button
              onClick={() => navigate("/login")}
              className="text-gray-600 border px-3 py-1.5 rounded-md hover:bg-gray-100 transition"
            >
              로그인
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
