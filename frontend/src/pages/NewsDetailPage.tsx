import { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";
import { api } from "../utils/axios";
import type { NewsDetail } from "../types/news";
import { ApiError, ErrorCode } from "../types/error";

export default function NewsDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [news, setNews] = useState<NewsDetail | null>(null);
  const [loading, setLoading] = useState(true);

  // UI 상태
  const [isLiked, setIsLiked] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [heartCount, setHeartCount] = useState(0);
  const [shareCount, setShareCount] = useState(0);

  // 뉴스 상세 조회 및 상태 확인
  useEffect(() => {
    if (!id) return;

    const fetchNewsDetail = async () => {
      try {
        setLoading(true);
        
        // 1. 뉴스 상세 조회
        const response = await api.get<NewsDetail>(`/api/news/${id}`);
        setNews(response.data);
        setHeartCount(response.data.heartCount);
        setShareCount(response.data.shareCount);
        
        // 2. 조회수 증가 API 호출 (실패해도 무시)
        api.post(`/api/news/${id}/view`).catch(() => {});

        // 3. 좋아요 여부 확인 (로그인 상태에서만)
        try {
          const likedResponse = await api.get<boolean>(`/api/news/${id}/heart/me`);
          setIsLiked(likedResponse.data);
        } catch (error) {
          // 비로그인(401) 에러는 무시
          if (error instanceof ApiError && !error.hasStatus(401)) {
            console.error("좋아요 여부 확인 실패:", error);
          }
        }

        // 4. 즐겨찾기 여부 확인 (로그인 상태에서만)
        try {
          const favoritedResponse = await api.get<boolean>(`/api/news/${id}/favorite/me`);
          setIsFavorite(favoritedResponse.data);
        } catch (error) {
          // 비로그인(401) 에러는 무시
          if (error instanceof ApiError && !error.hasStatus(401)) {
            console.error("즐겨찾기 여부 확인 실패:", error);
          }
        }
      } catch (error) {
        console.error("뉴스 상세 조회 실패:", error);
        if (error instanceof ApiError && error.is(ErrorCode.NEWS_NOT_FOUND)) {
          alert("존재하지 않는 뉴스입니다.");
          navigate("/news");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchNewsDetail();
  }, [id, navigate]);

  // 좋아요 토글 핸들러
  const handleLikeToggle = async () => {
    if (!id) return;

    try {
      if (isLiked) {
        // 좋아요 취소
        await api.delete(`/api/news/${id}/heart`);
        setHeartCount(prev => Math.max(0, prev - 1));
        setIsLiked(false);
      } else {
        // 좋아요 추가
        await api.post(`/api/news/${id}/heart`);
        setHeartCount(prev => prev + 1);
        setIsLiked(true);
      }
    } catch (error) {
      if (!(error instanceof ApiError)) {
        alert("좋아요 처리 중 오류가 발생했습니다.");
        return;
      }

      // ApiError로 깔끔하게 에러 처리
      if (error.hasStatus(401)) {
        alert("로그인이 필요한 기능입니다.");
        navigate("/login");
      } else if (error.is(ErrorCode.ALREADY_LIKED)) {
        alert(error.message);
        setIsLiked(true);
      } else if (error.is(ErrorCode.NOT_LIKED_YET)) {
        alert(error.message);
        setIsLiked(false);
      } else if (error.is(ErrorCode.NEWS_NOT_FOUND)) {
        alert(error.message);
        navigate("/news");
      } else {
        alert(error.message);
      }
    }
  };

  // 즐겨찾기 토글 핸들러
  const handleFavoriteToggle = async () => {
    if (!id) return;

    try {
      if (isFavorite) {
        // 즐겨찾기 취소
        await api.delete(`/api/news/${id}/favorite`);
        setIsFavorite(false);
        console.log("✅ 즐겨찾기 취소 성공");
      } else {
        // 즐겨찾기 추가
        await api.post(`/api/news/${id}/favorite`);
        setIsFavorite(true);
        console.log("✅ 즐겨찾기 추가 성공");
      }
    } catch (error) {
      if (!(error instanceof ApiError)) {
        alert("즐겨찾기 처리 중 오류가 발생했습니다.");
        return;
      }

      // ApiError로 깔끔하게 에러 처리
      if (error.hasStatus(401)) {
        alert("로그인이 필요한 기능입니다.");
        navigate("/login");
      } else if (error.is(ErrorCode.ALREADY_FAVORITED)) {
        alert(error.message);
        setIsFavorite(true);
      } else if (error.is(ErrorCode.NOT_FAVORITED_YET)) {
        alert(error.message);
        setIsFavorite(false);
      } else if (error.is(ErrorCode.NEWS_NOT_FOUND)) {
        alert(error.message);
        navigate("/news");
      } else {
        alert(error.message);
      }
    }
  };

  // 공유하기 핸들러
  const handleShare = async () => {
    if (!id) return;

    try {
      // 1. 클립보드에 URL 복사
      const url = window.location.href;
      await navigator.clipboard.writeText(url);

      // 2. 공유 수 증가 API 호출
      await api.post(`/api/news/${id}/share`);
      setShareCount(prev => prev + 1);

      alert("링크가 클립보드에 복사되었습니다!");
    } catch (error) {
      if (!(error instanceof ApiError)) {
        // 클립보드 API 에러 또는 기타 에러
        console.error("공유 처리 실패:", error);
        alert("공유 처리 중 오류가 발생했습니다.");
        return;
      }

      // API 에러 처리
      if (error.is(ErrorCode.NEWS_NOT_FOUND)) {
        alert(error.message);
        navigate("/news");
      } else {
        alert(error.message);
      }
    }
  };

  // 로딩 / 예외 처리
  if (loading) {
    return (
        <MainLayout>
          <div className="max-w-4xl mx-auto py-20 text-center text-gray-500">
            뉴스 불러오는 중...
          </div>
        </MainLayout>
    );
  }

  if (!news) {
    return (
        <MainLayout>
          <div className="max-w-4xl mx-auto py-20 text-center text-gray-500">
            존재하지 않는 뉴스입니다.
          </div>
        </MainLayout>
    );
  }

  // UI
  return (
    <MainLayout>
      <div className="max-w-4xl mx-auto">
        <nav className="text-sm text-gray-500 mb-6">
          <Link to="/">홈</Link> &gt; <Link to="/news">부동산뉴스</Link> &gt;{" "}
          <span className="font-medium text-gray-800">상세보기</span>
        </nav>

        {/* 대표 이미지와 즐겨찾기 */}
        <div className="relative mb-6">
          <svg
            onClick={handleFavoriteToggle}
            className={`absolute right-4 top-4 w-8 h-8 z-20 cursor-pointer transition-all ${
              isFavorite
                ? "fill-yellow-400 stroke-yellow-400"
                : "fill-none stroke-white"
            }`}
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            strokeWidth="2"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M11.48 3.499a.562.562 0 011.04 0l2.063 4.186a.563.563 0 00.424.308l4.624.672a.562.562 0 01.312.959l-3.348 3.26a.563.563 0 00-.162.498l.79 4.604a.563.563 0 01-.816.593L12 16.897l-4.132 2.17a.563.563 0 01-.816-.593l.79-4.604a.563.563 0 00-.162-.498L4.332 9.624a.562.562 0 01.312-.959l4.624-.672a.563.563 0 00.424-.308l2.063-4.186z"
            />
          </svg>
          <img
            src="https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=1000&q=80"
            alt={news.title}
            className="rounded-2xl shadow w-full object-cover h-72"
          />
        </div>

        {/* 제목 */}
        <h1 className="text-2xl md:text-3xl font-bold mb-3 leading-tight">
          {news.title}
        </h1>

        {/* 메타 정보 */}
        <div className="text-sm text-gray-500 mb-8 pb-4 border-b">
          <span>
            {new Date(news.createdDate).toLocaleDateString("ko-KR")}
          </span>
          {" · "}
          <span>조회수 {news.viewCount}</span>
        </div>

        {/* 본문 */}
        <article className="prose prose-lg max-w-none text-gray-700 leading-relaxed space-y-4">
          <div dangerouslySetInnerHTML={{ __html: news.content }} />
        </article>

        {/* 출처 */}
        <div className="text-sm text-gray-500 mt-8">
          출처:{" "}
          <a
              href={news.reference}
              target="_blank"
              rel="noopener noreferrer"
              className="underline"
          >
            {news.reference}
          </a>
        </div>

        {/* 좋아요 & 공유 */}
        <div className="flex items-center justify-center gap-4 py-8 mt-8 border-t border-b border-gray-100">
          <button
              onClick={handleLikeToggle}
              className={`flex items-center gap-2 px-6 py-2 rounded-full border-2 transition-all ${
                  isLiked
                      ? "border-red-500 bg-red-50"
                      : "border-gray-200 hover:bg-gray-50"
              }`}
          >
            <span>{isLiked ? "❤️" : "🤍"}</span>
            <span className="font-medium text-gray-700">
              좋아요 {heartCount}
            </span>
          </button>

          <button 
            onClick={handleShare}
            className="flex items-center gap-2 px-6 py-2 rounded-full border-2 border-gray-200 hover:bg-gray-50"
          >
            <span>🔗</span>{" "}
            <span className="font-medium text-gray-700">공유하기 {shareCount}</span>
          </button>
        </div>

        {/* 목록 이동 */}
        <div className="text-center mt-10">
          <button
              onClick={() => navigate(-1)}
              className="inline-block px-8 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
          >
            목록으로 돌아가기
          </button>
        </div>
      </div>
    </MainLayout>
  );
}
