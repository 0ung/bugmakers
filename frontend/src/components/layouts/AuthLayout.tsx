// src/components/layout/AuthLayout.tsx

interface AuthLayoutProps {
  children: React.ReactNode;
}

/**
 * 로그인 / 회원가입 페이지용 레이아웃
 * - 풀 화면 그라데이션 배경
 * - 중앙 카드 형태
 * - 상단에 로고만 노출
 */
export default function AuthLayout({ children }: AuthLayoutProps) {
  return (
    <div className="min-h-screen flex items-center justify-center p-6 animated-gradient">
      {/* 배경 애니메이션은 Tailwind layer나 글로벌 CSS에서 정의해도 됨 */}
      <div className="glass-effect w-full max-w-md sm:max-w-2xl rounded-3xl shadow-2xl p-8 bg-white/90 backdrop-blur">
        <div className="text-center mb-8">
          <a href="/">
            <img
              src="/static/images/마이홈.png"
              alt="마이홈 로고"
              className="h-20 mx-auto mb-4"
            />
          </a>
        </div>
        {children}
      </div>
    </div>
  );
}
