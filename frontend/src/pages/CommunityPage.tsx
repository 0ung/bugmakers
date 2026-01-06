import MainLayout from "../components/layouts/MainLayout";
import { useNavigate } from "react-router-dom";

export default function CommunityPage() {
  const navigate = useNavigate();
  const tags = [
    "전체",
    "집값",
    "노도강",
    "정책",
    "금리",
    "전세",
    "GTX",
    "재건축",
    "대출",
  ];

  const debates = [
    {
      id: 1,
      title: "25년 부동산 3차 정책 이후 집값은?",
      content:
        "정부의 3차 부동산 정책이 발표된 이후, 시장의 반응은 엇갈리고 있습니다...",
      votes: { a: 52, b: 48, total: 1234 },
      stats: { comments: 89, time: "5분 전", views: 3456 },
      tags: ["집값", "정책", "3차대책"],
      status: "🔥 열띤논쟁",
      badge: "HOT",
    },
    // ... 더 많은 데이터 추가 가능
  ];

  return (
    <MainLayout>
      <div className="max-w-6xl mx-auto space-y-8">
        {/* 글쓰기 버튼 */}
        <button
          onClick={() => navigate("/community/write")}
          className="w-full bg-blue-600 text-white py-4 rounded-xl text-lg font-bold hover:bg-blue-700 transition flex items-center justify-center gap-2"
        >
          <span>✏️</span> 새로운 토론 시작하기
        </button>

        {/* 인기 태그 필터 */}
        <section className="bg-white p-4 rounded-xl shadow">
          <div className="flex items-center gap-2 mb-3 text-sm font-medium">
            🏷️ 인기 태그
          </div>
          <div className="flex flex-wrap gap-2">
            {tags.map((tag) => (
              <button
                key={tag}
                className={`px-4 py-2 rounded-lg text-sm transition ${
                  tag === "전체"
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                }`}
              >
                #{tag}
              </button>
            ))}
          </div>
        </section>

        {/* 토론 목록 */}
        <div className="space-y-4">
          {debates.map((debate) => (
            <article
              key={debate.id}
              onClick={() => navigate(`/community/${debate.id}`)}
              className="cursor-pointer bg-white p-6 rounded-xl shadow hover:shadow-md transition border-l-4 border-transparent hover:border-blue-600 flex gap-6"
            >
              {/* 투표 썸네일 */}
              <div className="flex-shrink-0 w-32 h-32 bg-gradient-to-br from-red-50 to-orange-50 rounded-xl p-4 flex flex-col items-center justify-center border-2 border-red-200">
                <div className="text-[10px] text-gray-600 mb-1">
                  {debate.status}
                </div>
                <div className="flex items-center gap-1 font-bold">
                  <span className="text-blue-600">{debate.votes.a}%</span>
                  <span className="text-xs text-gray-400 font-normal">vs</span>
                  <span className="text-red-600">{debate.votes.b}%</span>
                </div>
                <div className="text-[10px] text-gray-500 mt-1">
                  {debate.votes.total}명
                </div>
              </div>

              {/* 내용 섹션 */}
              <div className="flex-1">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-xl font-bold">{debate.title}</h3>
                  {debate.badge && (
                    <span className="bg-red-100 text-red-700 text-xs px-2 py-1 rounded-full font-bold">
                      {debate.badge}
                    </span>
                  )}
                </div>
                <p className="text-gray-600 mb-3 line-clamp-2">
                  {debate.content}
                </p>
                <div className="flex items-center gap-4 text-sm text-gray-500 mb-3">
                  <span>💬 {debate.stats.comments}</span>
                  <span>⏰ {debate.stats.time}</span>
                  <span>👁️ {debate.stats.views}</span>
                </div>
                <div className="flex gap-2">
                  {debate.tags.map((t) => (
                    <span
                      key={t}
                      className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded-full"
                    >
                      #{t}
                    </span>
                  ))}
                </div>
              </div>
            </article>
          ))}
        </div>
      </div>
    </MainLayout>
  );
}
