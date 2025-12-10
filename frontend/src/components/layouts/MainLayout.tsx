// src/components/layout/MainLayout.tsx
import Header from "./Header";
import Footer from "./Footer";

interface MainLayoutProps {
  children: React.ReactNode;
}

/**
 * 일반 페이지 레이아웃
 * - 상단 공통 Header
 * - 하단 공통 Footer
 * - 가운데 컨텐츠 영역
 */
export default function MainLayout({ children }: MainLayoutProps) {
  return (
    <div className="min-h-screen flex flex-col bg-gray-50 text-gray-800">
      <Header />

      <main className="flex-1 max-w-7xl w-full mx-auto px-6 py-8">
        {children}
      </main>

      <Footer />
    </div>
  );
}
