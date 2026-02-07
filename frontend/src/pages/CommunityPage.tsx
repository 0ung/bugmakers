import React, { useState, useEffect, useRef, useCallback } from 'react';
import MainLayout from "../components/layouts/MainLayout";
import { useNavigate } from "react-router-dom";
import { api } from '../utils/axios';

// ... (인터페이스 정의 DebateVotes, forumstats, Debate, SliceResponse 그대로 유지) ...

export default function CommunityPage() {
  const navigate = useNavigate();

  const tags = [
    "전체", "집값", "노도강", "정책", "금리", "전세", "GTX", "재건축", "대출",
  ];
  const [selectedTag, setSelectedTag] = useState<string>("전체");

  const [forums, setForums] = useState<any[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0); // ⭐️ 시작 페이지는 0 (Backend Pageable 기준)

  const observerTarget = useRef<HTMLDivElement>(null);

  // ⭐️ API 요청 함수 (pageNumber를 인자로 받음)
  const fetchForums = useCallback(async (pageNum: number) => {
    // ⭐️ 추가된 방어 로직: 이미 로딩 중이거나 더 이상 데이터가 없거나,
    //    요청할 페이지가 현재 상태의 페이지보다 작으면 (이미 불러온 페이지면) 중복 호출 방지
    if (loading || !hasMore || (pageNum < page && pageNum !== 0)) {
        console.log(`Guard: loading=${loading}, hasMore=${hasMore}, pageNum=${pageNum}, current page=${page}. Returning.`);
        return;
    }

    setLoading(true);
    setError(null);

    try {
      console.log(`Fetching forums for page: ${pageNum}`); // ⭐️ 페이지 번호 로그 추가
      const response = await api.get<any>(
        `/api/forums`, {
          params: {
            page: pageNum,
            size: 10, // ⭐️ 한 번에 3개씩 가져오도록 수정
            sort: "createdDate,desc",
            // tag: selectedTag === "전체" ? "" : selectedTag,
          },
        }
      );

      const newForums = response.data.content;
      const isLastPage = response.data.last;

      setForums((prevForums) => {
        // ⭐️ 중요한 수정: 이전에 불러온 데이터가 이미 있는 경우 중복 추가 방지 (초기 로딩 시 특히 중요)
        if (pageNum === 0) {
            // 첫 페이지는 항상 덮어씌움 (필터링 등으로 초기화될 때)
            console.log(`Setting forums for page 0, total: ${newForums.length}`);
            return newForums;
        }
        // 그 외 페이지는 추가
        console.log(`Appending forums for page ${pageNum}, total new: ${newForums.length}, prev total: ${prevForums.length}`);
        return [...prevForums, ...newForums];
      });
      
      setHasMore(!isLastPage);
      setPage(pageNum + 1); // ⭐️ 다음 페이지 번호는 항상 현재 불러온 페이지 + 1

    } catch (err: any) {
      console.error('Failed to fetch forums:', err);
      setError('토론 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }, [loading, hasMore, page]); // page 상태가 변경될 때도 forums 함수가 업데이트되도록 함


  // ⭐️ 초기 데이터 로딩을 위한 useEffect (한 번만 실행)
  useEffect(() => {
    // ⭐️ 엄격한 방어 로직: page가 0이고 forums 배열이 비어있을 때만 첫 로딩
    if (page === 0 && forums.length === 0 && !loading && hasMore) {
        console.log("Initial fetch triggered by useEffect for page 0.");
        fetchForums(0);
    }
  }, [page, forums.length, loading, hasMore, fetchForums]); // 의존성 배열에 fetchForums 포함


  // ⭐️ 무한 스크롤 Intersection Observer 설정
  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        // observerTarget 요소가 화면에 나타났을 때만 실행
        // ⭐️ loading, hasMore, page 조건 추가
        if (entries[0].isIntersecting && hasMore && !loading) {
            console.log(`Intersection observed. Triggering fetch for page: ${page}`);
            fetchForums(page); // ⭐️ 현재 page 상태를 사용하여 다음 페이지 요청
        }
      },
      { threshold: 0.1 } // ⭐️ 타겟 요소의 10%만 보여도 트리거하도록 변경 (더 유연하게)
    );

    if (observerTarget.current) {
      observer.observe(observerTarget.current);
    }

    return () => {
      if (observerTarget.current) {
        observer.unobserve(observerTarget.current);
      }
    };
  }, [hasMore, loading, page, fetchForums]); // 의존성 배열에 관련 상태 및 함수 포함


  // 태그 클릭 핸들러 (태그 필터링 시 데이터 초기화 및 재로드)
  const handleTagClick = (tag: string) => {
    if (selectedTag === tag) return;

    setSelectedTag(tag);
    setForums([]);    // ⭐️ 데이터 초기화
    setPage(0);        // ⭐️ 페이지 번호 초기화
    setHasMore(true);  // ⭐️ hasMore 초기화

    // ⭐️ fetchForums(0) 호출
    // page가 0으로 초기화되었기 때문에, 위에 초기 데이터 로딩 useEffect가 다시 작동하여 fetchForums(0)을 호출하거나,
    // 여기서 명시적으로 호출하여 즉시 데이터를 다시 불러올 수 있습니다.
    // 여기서는 명시적으로 호출하는 방식을 사용 (즉각적인 반응을 위해)
    console.log(`Tag clicked: ${tag}. Resetting and fetching page 0.`);
    // fetchForums(0); // 직접 호출해도 되나, 첫 useEffect의 조건을 더 강하게 하면 거기서 처리되게 할 수도 있음.
                       // 현재는 초기화된 후 첫 useEffect에서 page === 0 조건을 다시 만족하므로 알아서 fetch 될 것임.
                       // 만약 즉각적인 로딩이 필요하다면 주석을 풀어 사용
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
          {forums.length === 0 && !loading && !error && page === 0 && ( // page === 0 조건 추가
            <p className="text-center text-gray-500">아직 토론이 없습니다.</p>
          )}

          {forums.map((forum) => (
            <article
              key={forum.id}
              onClick={() => navigate(`/community/${forum.id}`)}
              className="cursor-pointer bg-white p-6 rounded-xl shadow hover:shadow-md transition border-l-4 border-transparent hover:border-blue-600 flex gap-6"
            >
              {/* 투표 썸네일 */}
              <div className="flex-shrink-0 w-32 h-32 bg-gradient-to-br from-red-50 to-orange-50 rounded-xl p-4 flex flex-col items-center justify-center border-2 border-red-200">
                <div className="text-[10px] text-gray-600 mb-1">
                  {forum.status}
                </div>
                <div className="flex items-center gap-1 font-bold">
                  {/* <span className="text-blue-600">{forum.votes.a}%</span> */}
                  <span className="text-xs text-gray-400 font-normal">vs</span>
                  {/* <span className="text-red-600">{forum.votes.b}%</span> */}
                </div>
                <div className="text-[10px] text-gray-500 mt-1">
                  {/* {forum.votes.total}명 */}
                </div>
              </div>

              {/* 내용 섹션 */}
              <div className="flex-1">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-xl font-bold">{forum.title}</h3>
                  {forum.badge && (
                    <span className="bg-red-100 text-red-700 text-xs px-2 py-1 rounded-full font-bold">
                      {forum.badge}
                    </span>
                  )}
                </div>
                <p className="text-gray-600 mb-3 line-clamp-2">
                  {forum.content}
                </p>
                <div className="flex items-center gap-4 text-sm text-gray-500 mb-3">
                  {/* <span>💬 {forum.stats.comments}</span> */}
                  {/* <span>⏰ {forum.stats.time}</span> */}
                  {/* <span>👁️ {forum.stats.views}</span> */}
                </div>
                <div className="flex gap-2">
                  {/* {forum.tags.map((t) => (
                    <span
                      key={t}
                      className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded-full"
                    >
                      #{t}
                    </span>
                  ))} */}
                </div>
              </div>
            </article>
          ))}
        </div>

        {/* ⭐️ 무한 스크롤 타겟 요소 */}
        {/* 이 div가 화면에 나타나면 다음 페이지 데이터를 로드합니다. */}
        {hasMore && (
          <div ref={observerTarget} className="text-center p-4">
            {loading ? (
              <p>더 많은 토론을 불러오는 중...</p>
            ) : (
              <p className="text-gray-500">스크롤하여 더보기</p>
            )}
          </div>
        )}

        {!hasMore && forums.length > 0 && !loading && (
            <p className="text-center text-gray-500 p-4">마지막 토론입니다.</p>
        )}
      </div>
    </MainLayout>
  );
}