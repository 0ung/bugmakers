import { useState, useEffect, useRef, useCallback } from "react";
import MainLayout from "../components/layouts/MainLayout";
import { useNavigate } from "react-router-dom";
import { api } from "../utils/axios";

function formatRelativeTime(dateStr: string): string {
  const created = new Date(dateStr);
  const diffMs = Date.now() - created.getTime();
  const diffMin = Math.floor(diffMs / 1000 / 60);
  const diffHour = Math.floor(diffMin / 60);

  if (diffMin < 1) return "방금 전";
  if (diffMin < 60) return `${diffMin}분 전`;
  if (diffHour < 24) return `${diffHour}시간 전`;

  return created.toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).replace(/\. /g, "-").replace(".", "");
}

export default function CommunityPage() {
  const navigate = useNavigate();

  const tags = ["전체", "집값", "노도강", "정책", "금리", "전세", "GTX", "재건축", "대출"];
  const [selectedTag, setSelectedTag] = useState<string>("전체");

  const [forums, setForums] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState<boolean>(true);

  // 가드 로직은 ref로 관리 (stale closure 방지)
  const loadingRef = useRef(false);
  const hasMoreRef = useRef(true);
  const pageRef = useRef(0);

  const observerTarget = useRef<HTMLDivElement>(null);

  const fetchForums = useCallback(async (pageNum: number) => {
    if (loadingRef.current || !hasMoreRef.current) return;

    loadingRef.current = true;
    setLoading(true);
    setError(null);

    try {
      const response = await api.get<any>(`/api/forums`, {
        params: { page: pageNum, size: 10, sort: "createdDate,desc" },
      });

      const newForums = response.data.data.content;
      const isLastPage = response.data.data.last;

      setForums((prev) => (pageNum === 0 ? newForums : [...prev, ...newForums]));

      hasMoreRef.current = !isLastPage;
      setHasMore(!isLastPage);
      pageRef.current = pageNum + 1;
    } catch (err: any) {
      setError("토론 목록을 불러오는데 실패했습니다.");
    } finally {
      loadingRef.current = false;
      setLoading(false);
    }
  }, []); // 의존성 없음 → 안정적인 레퍼런스

  // 초기 로딩 (마운트 시 1회)
  useEffect(() => {
    fetchForums(0);
  }, [fetchForums]);

  // 무한 스크롤 Intersection Observer
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && hasMoreRef.current && !loadingRef.current) {
          fetchForums(pageRef.current);
        }
      },
      { threshold: 0.1 }
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => observer.disconnect();
  }, [fetchForums]);

  const handleTagClick = (tag: string) => {
    if (selectedTag === tag) return;

    setSelectedTag(tag);
    setForums([]);
    hasMoreRef.current = true;
    setHasMore(true);
    pageRef.current = 0;
    fetchForums(0);
  };

  if (error) {
    return (
      <MainLayout>
        <div className="max-w-6xl mx-auto p-4 text-red-600">오류 발생: {error}</div>
      </MainLayout>
    );
  }

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
                onClick={() => handleTagClick(tag)}
                className={`px-4 py-2 rounded-lg text-sm transition ${
                  selectedTag === tag
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
          {forums.length === 0 && !loading && !error && (
            <p className="text-center text-gray-500">아직 토론이 없습니다.</p>
          )}

          {forums.map((forum) => (
            <article
              key={forum.forumId}
              onClick={() => navigate(`/community/${forum.forumId}`)}
              className="cursor-pointer bg-white p-6 rounded-xl shadow hover:shadow-md transition border-l-4 border-transparent hover:border-blue-600 flex gap-6"
            >
              {/* 투표 현황 */}
              <div className="flex-shrink-0 w-32 h-32 bg-gradient-to-br from-blue-50 to-red-50 rounded-xl p-3 flex flex-col items-center justify-center border-2 border-gray-200 gap-0.5">
                {forum.topVotes?.length >= 2 ? (
                  <>
                    <span
                      className={`w-full text-center font-bold text-blue-700 leading-tight line-clamp-1 ${
                        forum.topVotes[0].count >= forum.topVotes[1].count ? "text-sm" : "text-xs"
                      }`}
                      title={forum.topVotes[0].name}
                    >
                      {forum.topVotes[0].name}
                    </span>
                    <span className="text-[11px] text-gray-400 font-semibold my-1">vs</span>
                    <span
                      className={`w-full text-center font-bold text-red-600 leading-tight line-clamp-1 ${
                        forum.topVotes[1].count > forum.topVotes[0].count ? "text-sm" : "text-xs"
                      }`}
                      title={forum.topVotes[1].name}
                    >
                      {forum.topVotes[1].name}
                    </span>
                  </>
                ) : forum.topVotes?.length === 1 ? (
                  <span
                    className="w-full text-center text-sm font-bold text-blue-700 leading-tight line-clamp-2"
                    title={forum.topVotes[0].name}
                  >
                    {forum.topVotes[0].name}
                  </span>
                ) : (
                  <span className="text-[10px] text-gray-400 text-center">아직 투표가 없습니다</span>
                )}
              </div>

              {/* 내용 섹션 */}
              <div className="flex-1">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-xl font-bold">{forum.title}</h3>
                </div>
                <p className="text-gray-600 mb-3 line-clamp-2">{forum.content}</p>
                <div className="flex justify-end">
                  <span className="text-xs text-gray-400">
                    {formatRelativeTime(forum.createdDate)}
                  </span>
                </div>
              </div>
            </article>
          ))}
        </div>

        {/* 무한 스크롤 타겟 */}
        {hasMore && (
          <div ref={observerTarget} className="text-center p-4">
            {loading ? (
              <p className="text-gray-400">불러오는 중...</p>
            ) : (
              <p className="text-gray-300">스크롤하여 더보기</p>
            )}
          </div>
        )}

        {!hasMore && forums.length > 0 && !loading && (
          <p className="text-center text-gray-400 p-4">마지막 토론입니다.</p>
        )}
      </div>
    </MainLayout>
  );
}
