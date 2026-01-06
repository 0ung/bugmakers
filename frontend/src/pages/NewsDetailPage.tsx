import { useState } from "react";
import { Link } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";

export default function NewsDetailPage() {
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(342);
  const [isFavorite, setIsFavorite] = useState(false);

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
            onClick={() => setIsFavorite(!isFavorite)}
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
            alt="뉴스 이미지"
            className="rounded-2xl shadow w-full object-cover h-72"
          />
        </div>

        <h1 className="text-2xl md:text-3xl font-bold mb-3 leading-tight">
          최근 오른 집값, 경기보다 ‘기대심리’가 더 큰 영향
        </h1>
        <div className="text-sm text-gray-500 mb-8 pb-4 border-b">
          <span>2025-11-11</span> · <span>시장동향</span> ·{" "}
          <span>마이홈 리서치팀</span>
        </div>

        <article className="prose prose-lg max-w-none text-gray-700 leading-relaxed space-y-4">
          <p>
            서울과 수도권을 중심으로 최근 집값 상승세가 이어지고 있는 가운데...
          </p>
          <p>
            한국은행은 11일 발표한 보고서에서 소비자 심리가 시세에 큰 영향을
            미친다고 밝혔습니다.
          </p>
        </article>

        {/* 좋아요 & 공유 */}
        <div className="flex items-center justify-center gap-4 py-8 mt-8 border-t border-b border-gray-100">
          <button
            onClick={() => {
              setIsLiked(!isLiked);
              setLikeCount((prev) => (isLiked ? prev - 1 : prev + 1));
            }}
            className={`flex items-center gap-2 px-6 py-2 rounded-full border-2 transition-all ${
              isLiked
                ? "border-red-500 bg-red-50"
                : "border-gray-200 hover:bg-gray-50"
            }`}
          >
            <span>{isLiked ? "❤️" : "🤍"}</span>
            <span className="font-medium text-gray-700">
              좋아요 {likeCount}
            </span>
          </button>
          <button className="flex items-center gap-2 px-6 py-2 rounded-full border-2 border-gray-200 hover:bg-gray-50">
            <span>🔗</span>{" "}
            <span className="font-medium text-gray-700">공유하기</span>
          </button>
        </div>

        <div className="text-center mt-10">
          <Link
            to="/news"
            className="inline-block px-8 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition"
          >
            목록으로 돌아가기
          </Link>
        </div>
      </div>
    </MainLayout>
  );
}
