import {useEffect, useRef, useState} from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";
import { api } from "../utils/axios";
import type {NewsItem, NewsPageResponse, NewsUIItem} from "../types/news.ts";

export default function NewsPage() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState<number[]>([]);

  const [newsList, setNewsList] = useState<NewsUIItem[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);

  const bottomRef = useRef<HTMLDivElement | null>(null);


  // 뉴스 목록 더미
  // const newsList = [
  //   {
  //     id: 1,
  //     category: "시장동향",
  //     title: "“최근 오른 집값, 경기보다 ‘기대심리’가 더 큰 영향”",
  //     description:
  //       "서울을 중심으로 이어진 집값 상승이 경기 상황보다 앞으로도 오를 것이라는 기대심리가 더 크게 작용했다는 분석이 나왔다.",
  //     date: "2025-11-11",
  //     image:
  //       "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=800&q=80",
  //   },
  //   // ... 나머지 카드 데이터들
  // ];

  // 뉴스 목록 불러오기
    const fetchNews = async (pageNumber: number) => {
      if (loading || !hasMore) return;

      try {
        setLoading(true);
        const response = await api.get<NewsPageResponse>('/api/news', {
          params: { page: pageNumber, size: 12 }
        });

        // API 데이터를 UI 형식으로 변환
        const formattedNews: NewsUIItem[] = response.data.content.map((news: NewsItem) => {
          // 제목에서 카테고리 추출: [부동산] → "부동산"
          const categoryMatch = news.title.match(/^\[([^\]]+)\]/);
          const category = categoryMatch ? categoryMatch[1] : "일반";
          const titleWithoutCategory = news.title.replace(/^\[[^\]]+\]\s*/, "");

          return {
            id: news.id,
            category: category,
            title: titleWithoutCategory,
            description: "", // API에 description 없음
            date: new Date(news.createdDate).toLocaleDateString('ko-KR'),
            image: "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80"
          };
        });

        // 기존 목록에 추가
        setNewsList(prev => [...prev, ...formattedNews]);

        setHasMore(response.data.number + 1 < response.data.totalPages);
        setPage(prev => prev + 1);
      } catch (error) {
        console.error('뉴스 불러오기 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    // 최초 로딩
    useEffect(() => {
      fetchNews(0);
  }, []);

  // 무한 스크롤 감지
  useEffect(() => {
    if (!bottomRef.current || !hasMore) return;

    const observer = new IntersectionObserver(
        ([entry]) => {
          if (entry.isIntersecting) {
            fetchNews(page);
          }
        },
        { threshold: 1 }
    );

    observer.observe(bottomRef.current);
    return () => observer.disconnect();
  }, [page, hasMore]);

  const toggleFavorite = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    setFavorites(prev =>
        prev.includes(id)
            ? prev.filter(favId => favId !== id)
            : [...prev, id]
    );
  };

  // 로딩 중
  if (!loading && newsList.length === 0) {
    return (
        <MainLayout>
          <div className="max-w-7xl mx-auto text-center py-20">
            <div className="text-gray-500">뉴스를 불러오는 중...</div>
          </div>
        </MainLayout>
    );
  }

  // 뉴스가 없을 때
  if (newsList.length === 0) {
    return (
        <MainLayout>
          <div className="max-w-7xl mx-auto text-center py-20">
            <div className="text-gray-500">등록된 뉴스가 없습니다.</div>
          </div>
        </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="max-w-7xl mx-auto">
        <nav className="text-sm text-gray-500 mb-4">
          홈 &gt; <span className="font-medium">부동산뉴스</span>
        </nav>

        <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {newsList.map((news) => (
            <article
              key={news.id}
              onClick={() => navigate(`/news/${news.id}`)}
              className="group cursor-pointer bg-white rounded-2xl shadow overflow-hidden hover:-translate-y-1 hover:shadow-xl transition-all duration-200"
            >
              <div className="relative aspect-video overflow-hidden">
                <svg
                  onClick={(e) => toggleFavorite(e, news.id)}
                  className={`absolute right-3 top-3 w-6 h-6 z-20 cursor-pointer transition-all ${
                    favorites.includes(news.id)
                      ? "fill-yellow-400 stroke-yellow-400"
                      : "fill-none stroke-gray-300"
                  }`}
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  strokeWidth="1.8"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M11.48 3.499a.562.562 0 011.04 0l2.063 4.186a.563.563 0 00.424.308l4.624.672a.562.562 0 01.312.959l-3.348 3.26a.563.563 0 00-.162.498l.79 4.604a.563.563 0 01-.816.593L12 16.897l-4.132 2.17a.563.563 0 01-.816-.593l.79-4.604a.563.563 0 00-.162-.498L4.332 9.624a.562.562 0 01.312-.959l4.624-.672a.563.563 0 00.424-.308l2.063-4.186z"
                  />
                </svg>
                <img
                  src={news.image}
                  alt={news.title}
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="p-4">
                <div className="text-xs text-blue-600 font-semibold mb-1">
                  {news.category}
                </div>
                <h3 className="text-lg font-semibold mb-2 line-clamp-2">
                  {news.title}
                </h3>
                <p className="text-sm text-gray-600 mb-2 line-clamp-2">
                  {news.description}
                </p>
                <div className="text-xs text-gray-400">{news.date}</div>
              </div>
            </article>
          ))}
        </section>

        <div ref={bottomRef} className="h-10" />
        {loading && (
            <div className="text-center text-gray-500 py-6">
              뉴스 불러오는 중...
            </div>
        )}
      </div>
    </MainLayout>
  );
}
