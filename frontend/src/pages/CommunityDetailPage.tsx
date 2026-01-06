import { useState } from "react";
import MainLayout from "../components/layouts/MainLayout";

export default function CommunityDetailPage() {
  //   const [isLiked, setIsLiked] = useState(false);
  const [votedOption, setVotedOption] = useState<string | null>(null);

  return (
    <MainLayout>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white p-8 rounded-2xl shadow">
            <h2 className="text-2xl font-bold mb-4">
              25년 부동산 3차 정책 이후 집값은?
            </h2>
            <div className="text-sm text-gray-500 mb-6 border-b pb-4">
              작성자: 관리자 · 2025-11-12 · 조회 1,234
            </div>

            <p className="text-gray-700 leading-relaxed mb-8">
              정부의 3차 부동산 정책이 발표된 이후, 시장의 반응은 엇갈리고
              있습니다. 전문가들의 의견과 여러분의 생각을 나눠주세요.
            </p>

            {/* 투표 섹션 */}
            <div className="bg-gray-50 p-6 rounded-xl border border-gray-200">
              <h3 className="font-bold mb-4">📊 실시간 투표 현황</h3>
              <div className="grid grid-cols-2 gap-4 mb-6">
                {["상승한다", "하락한다"].map((option) => (
                  <button
                    key={option}
                    onClick={() => setVotedOption(option)}
                    className={`p-4 rounded-xl border-2 transition-all ${
                      votedOption === option
                        ? "border-blue-600 bg-blue-600 text-white scale-105"
                        : "border-blue-200 bg-white hover:bg-blue-50"
                    }`}
                  >
                    <span className="font-bold">{option}</span>
                  </button>
                ))}
              </div>
            </div>
          </div>

          {/* 댓글 섹션 생략... */}
        </div>

        {/* 사이드바 (AI 도우미) */}
        <aside className="space-y-6">
          <div className="bg-blue-50 p-6 rounded-2xl border border-blue-100">
            <h4 className="font-bold mb-3 flex items-center gap-2">
              🤖 AI 도우미
            </h4>
            <input
              type="text"
              placeholder="정책에 대해 궁금한 점을 물어보세요"
              className="w-full p-2 border rounded-lg text-sm mb-2"
            />
            <button className="w-full bg-blue-600 text-white py-2 rounded-lg text-sm">
              GPT 분석 요청
            </button>
          </div>
        </aside>
      </div>
    </MainLayout>
  );
}
