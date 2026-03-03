import { useState} from "react";
import { useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";
import { api } from "../utils/axios";

// ----------------------------------------
// 1. 타입 정의 (Interfaces)
// ----------------------------------------

// 투표 항목 하나의 타입
interface VoteOption {
  id: number; // 고유 ID (삭제 시 특정 항목 식별)
  value: string; // 투표 항목 내용
}

// 폼 전체 데이터의 타입
interface FormData {
  title: string;
  content: string;
}


// ----------------------------------------
// 2. 컴포넌트 구현
// ----------------------------------------

// 고유 ID 생성을 위한 간단한 헬퍼 변수 (클라이언트 사이드에서만 사용)
let nextVoteOptionId = 0;

export default function CommunityWritePage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState<FormData>({
    title: '',
    content: '',
  });

  // 투표 항목들을 VoteOption 객체 배열로 관리
  const [voteList, setVoteList] = useState<VoteOption[]>([
    { id: nextVoteOptionId++, value: "" }, // 초기 2개 항목
    { id: nextVoteOptionId++, value: "" },
  ]);

  const [voteDeadline, setVoteDeadline] = useState<string>("");

  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  /**
   * 일반 Input (제목, 내용) 변경 핸들러
   */
  const handleInputData = (e: any): void => {
    const { name, value } = e.target;
    setFormData(prevData => ({
      ...prevData,
      [name]: value,
    }));
  };

  /**
   * 투표 항목 Input 변경 핸들러
   */
  const handleVoteInputChange = (id: number, e: any): void => {
    const { value } = e.target;
    setVoteList(prevList =>
      prevList.map(option =>
        option.id === id ? { ...option, value: value } : option
      )
    );
  };

  /**
   * 투표 항목 추가
   */
  const addVote = (): void => {
    if (voteList.length < 5) { // 최대 5개 항목 제한
      setVoteList(prevList => [...prevList, { id: nextVoteOptionId++, value: "" }]);
    }
  };

  /**
   * 투표 항목 제거
   */
  const removeVote = (idToRemove: number): void => {
    if (voteList.length <= 2) { // 최소 2개 항목 유지
      alert("투표 항목은 최소 2개여야 합니다.");
      return;
    }
    setVoteList(prevList => prevList.filter(option => option.id !== idToRemove));
  };


  /**
   * 등록 버튼 클릭 핸들러
   */
  const register = async (e: any) => {
    e.preventDefault();

    setLoading(true);
    setError(null);
    setSuccess(false);

    // 유효성 검사
    if (!formData.title.trim() || !formData.content.trim()) {
        setError("제목과 내용을 입력해주세요.");
        setLoading(false);
        return;
    }
    if (voteList.length < 2) {
        setError("투표 항목은 최소 2개 이상이어야 합니다.");
        setLoading(false);
        return;
    }
    const hasEmptyVote = voteList.some(vote => !vote.value.trim()); // 객체 value로 검사
    if (hasEmptyVote) {
        setError("비어있는 투표 항목이 있습니다. 모든 항목을 채우거나 삭제해주세요.");
        setLoading(false);
        return;
    }

    try {
      const payload = {
        title: formData.title,
        content: formData.content,
        // 백엔드에 보낼 때는 항목의 value만 배열로 추출 (원하는 형식에 따라 변경)
        voteList: voteList.map(option => {
          return { name : option.value};
        }),
        voteDeadline: voteDeadline || null,
      };

      console.log("전송될 데이터:", payload);

      // axios 요청 (API 엔드포인트 수정 필요)
      const response = await api.post<any>('/api/forum', payload);
      console.log('게시글 등록 성공:', response.data.data);
      setSuccess(true);

      // 성공 후 커뮤니티 목록 페이지로 이동
      navigate('/community');

    } catch (err: any) {
      console.error('게시글 등록 실패:', err);
      setError('게시글 등록에 실패했습니다: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };


  return (
    <MainLayout>
      <form onSubmit={register} className="max-w-4xl mx-auto bg-white p-8 rounded-2xl shadow space-y-6">
        <h2 className="text-2xl font-bold">토론 주제 작성</h2>

        {loading && <p>등록 중...</p>}
        {error && <p className="text-red-500">{error}</p>}
        {success && <p className="text-green-500">게시글이 성공적으로 등록되었습니다!</p>}

        <div>
          <label className="block text-sm font-medium mb-2">제목 *</label>
          <input
            name="title"
            value={formData.title}
            onChange={handleInputData}
            required
            className="w-full border p-3 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
            placeholder="주제를 입력하세요"
            disabled={loading}
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">내용 *</label>
          <textarea
            name="content"
            value={formData.content}
            onChange={handleInputData}
            className="w-full border p-3 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
            placeholder="내용을 입력하세요"
            disabled={loading}
            rows={5}
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">
            투표 항목 * (최소 2개, 최대 5개)
          </label>
          <div className="space-y-3">
            {voteList.map((option, idx) => ( // option 객체와 idx를 모두 사용
              <div key={option.id} className="flex items-center space-x-2"> {/* key로 option.id 사용 */}
                <input
                  className="w-full border p-3 rounded-lg"
                  placeholder={`항목 ${idx + 1}`} 
                  value={option.value} // 객체의 value 속성 사용
                  onChange={e => handleVoteInputChange(option.id, e)} // id와 이벤트 전달
                  disabled={loading}
                />
                {voteList.length > 2 && ( // 2개 초과일 때만 삭제 버튼 활성화
                  <button
                    type="button"
                    onClick={() => removeVote(option.id)} // option.id를 통해 특정 항목 제거
                    className="p-3 text-red-600 border border-red-600 rounded-lg disabled:opacity-50"
                    disabled={loading}
                  >
                    삭제
                  </button>
                )}
              </div>
            ))}
          </div>
          {voteList.length < 5 && (
            <button
              type="button"
              onClick={addVote}
              className="mt-3 text-blue-600 font-medium disabled:opacity-50"
              disabled={loading}
            >
              + 항목 추가
            </button>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">
            투표 마감일 (선택사항)
          </label>
          <input
            type="datetime-local"
            value={voteDeadline}
            onChange={e => setVoteDeadline(e.target.value)}
            className="w-full border p-3 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
            disabled={loading}
          />
          <p className="text-xs text-gray-400 mt-1">
            설정하지 않으면 마감 없이 계속 투표할 수 있습니다.
          </p>
        </div>

        <button
          type="submit"
          className="w-full bg-blue-600 text-white py-4 rounded-xl font-bold disabled:opacity-50"
          disabled={loading}
        >
          {loading ? '등록 중...' : '등록하기'}
        </button>
      </form>
    </MainLayout>
  );
}