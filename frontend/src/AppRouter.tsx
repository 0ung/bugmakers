// src/router/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "../src/pages/HomePage";
import LoginPage from "./pages/LoginPage";
import CommunityPage from "./pages/CommunityPage";
import CommunityDetailPage from "./pages/CommunityDetailPage";
import CommunityWritePage from "./pages/CommunityWritePage";
import NewsPage from "./pages/NewsPage";
import NewsDetailPage from "./pages/NewsDetailPage";
import OAuth2RedirectHandler from "./pages/OAuth2RedirectHandler";
// ... 나머지 페이지들 import

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 메인 및 인증 */}
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        {/* OAuth2 콜백 처리 */}
        <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
        {/* 커뮤니티 관련 */}
        <Route path="/community" element={<CommunityPage />} />
        <Route path="/community/:id" element={<CommunityDetailPage />} />
        <Route path="/community/write" element={<CommunityWritePage />} />
        {/* 뉴스 */}
        <Route path="/news" element={<NewsPage />} />
        <Route path="/news/:id" element={<NewsDetailPage />} />{" "}
      </Routes>
    </BrowserRouter>
  );
}
