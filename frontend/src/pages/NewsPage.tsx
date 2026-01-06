import { useState } from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";

export default function NewsPage() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState<number[]>([]);

  const newsList = [
    {
      id: 1,
      category: "시장동향",
      title: "“최근 오른 집값, 경기보다 ‘기대심리’가 더 큰 영향”",
      description:
        "서울을 중심으로 이어진 집값 상승이 경기 상황보다 앞으로도 오를 것이라는 기대심리가 더 크게 작용했다는 분석이 나왔다.",
      date: "2025-11-11",
      image:
        "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=800&q=80",
    },
    // ... 나머지 카드 데이터들
  ];

  const toggleFavorite = (e: React.MouseEvent, id: number) => {
    e.stopPropagation(); // 카드 클릭 이벤트 전파 방지
    setFavorites((prev) =>
      prev.includes(id) ? prev.filter((favId) => favId !== id) : [...prev, id]
    );
  };

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

        <div className="mt-12 text-center">
          <button className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition">
            더 보기
          </button>
        </div>
      </div>
    </MainLayout>
  );
}
