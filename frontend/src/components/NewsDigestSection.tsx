import { useEffect, useState } from 'react';
import { digestApi } from '@/utils/digestApi';
import type { DigestResponse } from '@/types/digest';
import { NewsCategoryLabel } from '@/types/digest';

export default function NewsDigestSection() {
  const [digests, setDigests] = useState<DigestResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState('');

  useEffect(() => {
    // 어제 날짜로 초기화
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const dateStr = yesterday.toISOString().split('T')[0];
    setSelectedDate(dateStr);
    fetchDigests(dateStr);
  }, []);

  const fetchDigests = async (date: string) => {
    try {
      setLoading(true);
      const data = await digestApi.getDigestsByDate(date);
      setDigests(data);
    } catch (error) {
      console.error('다이제스트 조회 실패:', error);
      setDigests([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = e.target.value;
    setSelectedDate(newDate);
    fetchDigests(newDate);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center py-12">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (digests.length === 0) {
    return null; // 데이터 없으면 아예 안 보여줌
  }

  return (
    <div>
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800 mb-1">
            🤖 AI 뉴스 요약
          </h2>
          <p className="text-sm text-gray-600">
            AI가 분석한 날짜별 뉴스 트렌드
          </p>
        </div>
        
        {/* 날짜 선택 */}
        <input
          type="date"
          value={selectedDate}
          onChange={handleDateChange}
          className="px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
        />
      </div>

      {/* 다이제스트 카드 (가로 스크롤) */}
      <div className="overflow-x-auto pb-4">
        <div className="flex gap-4 min-w-max">
          {digests.map((digest) => (
            <div
              key={digest.id}
              className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl shadow-sm hover:shadow-md transition-shadow p-5 w-80 flex-shrink-0"
            >
              {/* 카테고리 + 개수 */}
              <div className="flex items-center justify-between mb-3">
                <span className="inline-block px-3 py-1 bg-blue-600 text-white text-xs font-semibold rounded-full">
                  {NewsCategoryLabel[digest.category]}
                </span>
                <span className="text-xs text-gray-500">
                  {digest.newsCount}개 뉴스
                </span>
              </div>

              {/* 요약 내용 */}
              <div className="text-sm text-gray-700 leading-relaxed line-clamp-4">
                {digest.summary}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
