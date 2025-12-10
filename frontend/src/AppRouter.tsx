// src/router/AppRouter.tsx
import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "../src/pages/HomePage";
import LoginPage from "./pages/LoginPage";
// ... 나머지 페이지들 import

export default function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />

        <Route path="/login" element={<LoginPage />} />
        {/* <Route path="/join" element={<JoinPage />} /> */}

        {/* 추후 */}
        {/* <Route path="/news" element={<NewsPage />} /> */}
      </Routes>
    </BrowserRouter>
  );
}
