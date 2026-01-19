import {useEffect, useRef, useState, useCallback} from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";
import { api } from "../utils/axios";
import type {NewsItem, NewsPageResponse, NewsUIItem} from "../types/news.ts";
import { ApiError, ErrorCode } from "../types/error";

// Debounce 함수
function debounce<T extends (...args: any[]) => any>(
    func: T,
    wait: number
): (...args: Parameters<T>) => void {
  let timeout: number; // NodeJS.Timeout → number
  return (...args: Parameters<T>) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
}

export default function NewsPage() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState<Set<number>>(new Set());

  const [newsList, setNewsList] = useState<NewsUIItem[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<string>("전체");
  const [searchKeyword, setSearchKeyword] = useState<string>("");
  const [categories, setCategories] = useState<string[]>([]);

  const bottomRef = useRef<HTMLDivElement | null>(null);
  const isMounted = useRef(false); // React Strict Mode 대응

  // 카테고리 목록 불러오기
  const fetchCategories = async () => {
    try {
      const response = await api.get<string[]>('/api/news/categories');
      setCategories(["전체", ...response.data]);
    } catch (error) {
      console.error('카테고리 목록 불러오기 실패:', error);
      setCategories(["전체"]);
    }
  };

  // 즐겨찾기 상태 로드 (로그인 사용자만)
  const loadFavorites = async (newsIds: number[]): Promise<Set<number>> => {
    try {
      // 각 뉴스별 즐겨찾기 여부 확인
      const promises = newsIds.map(id => 
        api.get<boolean>(`/api/news/${id}/favorite/me`)
          .then(res => ({ id, isFavorited: res.data }))
          .catch(() => ({ id, isFavorited: false })) // 에러는 무시 (비로그인)
      );

      const results = await Promise.all(promises);
      const newFavorites = new Set<number>();
      
      results.forEach(({ id, isFavorited }) => {
        if (isFavorited) {
          newFavorites.add(id);
        }
      });

      return newFavorites;
    } catch (error) {
      console.error('즐겨찾기 상태 로드 실패:', error);
      return new Set<number>();
    }
  };

  // 뉴스 목록 불러오기 (서버에서 필터링)
  const fetchNews = async (
    pageNumber: number,
    tag: string,
    keyword: string,
    append: boolean = true
  ) => {
    if (loading) return;

    try {
      setLoading(true);
      
      const params: any = { 
        page: pageNumber, 
        size: 12 
      };

      // "전체"가 아닌 태그만 파라미터로 전달
      if (tag !== "전체") {
        params.tag = tag;
      }
      
      // 검색어가 있으면 추가
      if (keyword && keyword.trim() !== "") {
        params.keyword = keyword.trim();
      }

      const response = await api.get<NewsPageResponse>('/api/news', { params });

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

      // 새로 로드한 뉴스들의 즐겨찾기 상태 확인
      const newsIds = formattedNews.map(news => news.id);
      const newFavorites = await loadFavorites(newsIds);

      // append가 true면 기존 목록에 추가, false면 새로 시작
      setNewsList(prev => append ? [...prev, ...formattedNews] : formattedNews);
      setFavorites(prev => {
        const merged = new Set(prev);
        newFavorites.forEach(id => merged.add(id));
        return merged;
      });

      setHasMore(response.data.number + 1 < response.data.totalPages);
      setPage(prev => prev + 1);
      // setPage(response.data.number + 1);
    } catch (error) {
      console.error('뉴스 불러오기 실패:', error);
    } finally {
      setLoading(false);
      setInitialLoading(false);
    }
  };

  // Debounced 검색 함수 (500ms 대기)
  /* : FRONT(스크롤 내리기 전 12개를 실시간 검색 진행) + BACK(5초후 API로 전체 검색 진행 하여 자연스럽게 실시간 검색처럼 보이게함)  */
  const debouncedFetchNews = useCallback(
    debounce((tag: string, keyword: string) => {
      // 검색어/태그 변경 시 목록 초기화하고 첫 페이지부터 시작
      setNewsList([]);
      setPage(0);
      setHasMore(true);
      fetchNews(0, tag, keyword, false);
    }, 500),
    []
  );

  // 최초 로딩 (React Strict Mode 대응)
  useEffect(() => {
    if (isMounted.current) return;
    isMounted.current = true;
    
    fetchCategories();
    fetchNews(0, "전체", "", false);
  }, []);

  // 카테고리 변경 시 즉시 검색
  useEffect(() => {
    if (!isMounted.current) return;
    
    setNewsList([]);
    setPage(0);
    setHasMore(true);
    fetchNews(0, selectedCategory, searchKeyword, false);
  }, [selectedCategory]);

  // 검색어 변경 시 debounced 검색
  useEffect(() => {
    if (!isMounted.current) return;
    
    debouncedFetchNews(selectedCategory, searchKeyword);
  }, [searchKeyword]);

  // 무한 스크롤 감지
  useEffect(() => {
    if (!bottomRef.current || !hasMore || loading) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !loading) {
          fetchNews(page, selectedCategory, searchKeyword, true);
        }
      },
      { threshold: 1 }
    );

    observer.observe(bottomRef.current);
    return () => observer.disconnect();
  }, [page, hasMore, loading, selectedCategory, searchKeyword]);

  // 카테고리 선택
  const handleCategoryClick = (category: string) => {
    setSelectedCategory(category);
  };

  // 검색어 입력
  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchKeyword(e.target.value);
  };

  // 검색 초기화
  const handleSearchClear = () => {
    setSearchKeyword("");
  };

  // 즐겨찾기 토글
  const toggleFavorite = async (e: React.MouseEvent, id: number) => {
    e.stopPropagation();

    const isFavorited = favorites.has(id);

    try {
      if (isFavorited) {
        // 즐겨찾기 취소
        await api.delete(`/api/news/${id}/favorite`);
        setFavorites(prev => {
          const newSet = new Set(prev);
          newSet.delete(id);
          return newSet;
        });
      } else {
        // 즐겨찾기 추가
        await api.post(`/api/news/${id}/favorite`);
        setFavorites(prev => new Set(prev).add(id));
      }
    } catch (error) {
      if (!(error instanceof ApiError)) {
        alert("즐겨찾기 처리 중 오류가 발생했습니다.");
        return;
      }

      if (error.hasStatus(401)) {
        alert("로그인이 필요한 기능입니다.");
        navigate("/login");
      } else if (error.is(ErrorCode.ALREADY_FAVORITED)) {
        alert(error.message);
        setFavorites(prev => new Set(prev).add(id));
      } else if (error.is(ErrorCode.NOT_FAVORITED_YET)) {
        alert(error.message);
        setFavorites(prev => {
          const newSet = new Set(prev);
          newSet.delete(id);
          return newSet;
        });
      } else if (error.is(ErrorCode.NEWS_NOT_FOUND)) {
        alert(error.message);
      } else {
        alert(error.message);
      }
    }
  };

  // 초기 로딩 중
  if (initialLoading) {
    return (
      <MainLayout>
        <div className="max-w-7xl mx-auto text-center py-20">
          <div className="text-gray-500">뉴스를 불러오는 중...</div>
        </div>
      </MainLayout>
    );
  }

  // UI
  return (
    <MainLayout>
      <div className="max-w-7xl mx-auto">
        {/* 네비게이터 */}
        <nav className="text-sm text-gray-500 mb-6">
          홈 &gt; <span className="font-medium">부동산뉴스</span>
        </nav>

        {/* 카테고리 탭 + 검색바 */}
        <div className="mb-8 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          {/* 카테고리 탭 */}
          <div className="flex flex-wrap gap-2">
            {categories.map((category) => (
              <button
                key={category}
                onClick={() => handleCategoryClick(category)}
                className={`
                  px-6 py-2.5 rounded-full font-medium text-sm
                  transition-all duration-200
                  ${selectedCategory === category
                    ? "bg-blue-600 text-white shadow-md shadow-blue-200"
                    : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                  }
                `}
              >
                {category}
              </button>
            ))}
          </div>

          {/* 검색바 */}
          <div className="relative">
            <input
              type="text"
              placeholder="뉴스 검색..."
              value={searchKeyword}
              onChange={handleSearchChange}
              className="
                w-full lg:w-80 pl-11 pr-10 py-2.5 
                rounded-full border border-gray-300
                focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                transition-all duration-200
              "
            />
            {/* 돋보기 아이콘 */}
            <svg
              className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            {/* X 버튼 */}
            {searchKeyword && (
              <button
                onClick={handleSearchClear}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <svg
                  className="w-5 h-5"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            )}
          </div>
        </div>

        {/* 뉴스 목록 */}
        {newsList.length === 0 && !loading ? (
          <div className="text-center py-20">
            <div className="text-gray-500">
              {searchKeyword 
                ? `'${searchKeyword}'에 대한 검색 결과가 없습니다.`
                : `'${selectedCategory}' 카테고리에 해당하는 뉴스가 없습니다.`
              }
            </div>
          </div>
        ) : (
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
                      favorites.has(news.id)
                        ? "fill-yellow-400 stroke-yellow-400"
                        : "fill-none stroke-gray-300 hover:stroke-yellow-400"
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
        )}

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
