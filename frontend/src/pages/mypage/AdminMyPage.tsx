import { useState } from "react";
import { useAuthStore } from "../../stores/authStore";
import MainLayout from "../../components/layouts/MainLayout";

type AdminTab = "dashboard" | "reports" | "popular" | "users" | "settings";

export default function AdminMyPage() {
  const { user } = useAuthStore();
  const [activeTab, setActiveTab] = useState<AdminTab>("dashboard");

  if (!user) return null;

  return (
    <MainLayout>
      <main className="pt-10 pb-16 max-w-7xl mx-auto px-6">
        {/* 관리자 프로필 섹션 */}
        <section className="bg-gradient-to-r from-red-500 to-orange-500 rounded-3xl shadow-2xl p-8 text-white mb-8">
          <div className="flex items-center gap-6">
            {/* 관리자 아이콘 */}
            <div className="w-24 h-24 bg-white/30 backdrop-blur-sm rounded-full flex items-center justify-center text-5xl border-4 border-white">
              👑
            </div>

            {/* 관리자 정보 */}
            <div className="flex-1">
              <h1 className="text-3xl font-bold mb-2">{user.nickname}님 (관리자)</h1>
              <p className="text-white/80 mb-3">{user.email?.address || "이메일 정보 없음"}</p>
              <div className="flex gap-3">
                <span className="bg-white/30 backdrop-blur-sm px-4 py-1 rounded-full text-sm">
                  👑 관리자 권한
                </span>
                <span className="bg-white/30 backdrop-blur-sm px-4 py-1 rounded-full text-sm">
                  🛡️ 시스템 관리
                </span>
              </div>
            </div>

            {/* 시스템 상태 */}
            <div className="bg-white/20 backdrop-blur-sm px-6 py-4 rounded-xl">
              <div className="text-sm text-white/80">시스템 상태</div>
              <div className="text-2xl font-bold mt-1">🟢 정상</div>
            </div>
          </div>
        </section>

        {/* 전체 통계 (TODO: API 연동) */}
        <section className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center justify-between mb-3">
              <span className="text-gray-600">전체 조회수</span>
              <span className="text-3xl">👀</span>
            </div>
            <div className="text-3xl font-bold text-blue-600">0</div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center justify-between mb-3">
              <span className="text-gray-600">전체 좋아요</span>
              <span className="text-3xl">️❤</span>
            </div>
            <div className="text-3xl font-bold text-green-600">0</div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center justify-between mb-3">
              <span className="text-gray-600">신고 건수</span>
              <span className="text-3xl">🚨</span>
            </div>
            <div className="text-3xl font-bold text-red-600">0</div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center justify-between mb-3">
              <span className="text-gray-600">활동 사용자</span>
              <span className="text-3xl">👥</span>
            </div>
            <div className="text-3xl font-bold text-purple-600">0</div>
          </div>
        </section>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* 왼쪽: 관리자 메뉴 */}
          <aside className="space-y-3">
            <button
              onClick={() => setActiveTab("dashboard")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "dashboard"
                  ? "bg-red-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              📊 통계 대시보드
            </button>
            <button
              onClick={() => setActiveTab("reports")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "reports"
                  ? "bg-red-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              🚨 신고 관리
            </button>
            <button
              onClick={() => setActiveTab("popular")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "popular"
                  ? "bg-red-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              🔥 인기 콘텐츠
            </button>
            <button
              onClick={() => setActiveTab("users")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "users"
                  ? "bg-red-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              👥 사용자 순위
            </button>
            <button
              onClick={() => setActiveTab("settings")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "settings"
                  ? "bg-red-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              ⚙️ 시스템 설정
            </button>
          </aside>

          {/* 오른쪽: 관리 콘텐츠 */}
          <div className="lg:col-span-2">
            {activeTab === "dashboard" && <DashboardContent />}
            {activeTab === "reports" && <ReportsContent />}
            {activeTab === "popular" && <PopularContent />}
            {activeTab === "users" && <UsersContent />}
            {activeTab === "settings" && <SettingsContent />}
          </div>
        </div>
      </main>
    </MainLayout>
  );
}

// 관리자 탭별 컴포넌트 (TODO: API 연동)
function DashboardContent() {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>📊</span>
          <span>통계 대시보드</span>
        </h2>
        <div className="grid grid-cols-2 gap-4">
          <div className="p-4 bg-blue-50 rounded-xl">
            <div className="text-sm text-gray-600 mb-1">뉴스 조회 TOP 1</div>
            <div className="font-bold text-blue-600">- (데이터 없음)</div>
          </div>
          <div className="p-4 bg-green-50 rounded-xl">
            <div className="text-sm text-gray-600 mb-1">포럼 조회 TOP 1</div>
            <div className="font-bold text-green-600">- (데이터 없음)</div>
          </div>
          <div className="p-4 bg-yellow-50 rounded-xl">
            <div className="text-sm text-gray-600 mb-1">좋아요 TOP 1</div>
            <div className="font-bold text-yellow-600">- (데이터 없음)</div>
          </div>
          <div className="p-4 bg-purple-50 rounded-xl">
            <div className="text-sm text-gray-600 mb-1">공유 TOP 1</div>
            <div className="font-bold text-purple-600">- (데이터 없음)</div>
          </div>
        </div>
      </div>
    </div>
  );
}

function ReportsContent() {
  return (
    <div className="bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
        <span>🚨</span>
        <span>신고 관리</span>
      </h2>
      <div className="text-center py-12 text-gray-500">
        처리할 신고가 없습니다.
      </div>
    </div>
  );
}

function PopularContent() {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>🔥</span>
          <span>인기 글 TOP 10</span>
        </h2>
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>💬</span>
          <span>인기 댓글 TOP 10</span>
        </h2>
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      </div>
    </div>
  );
}

function UsersContent() {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>✍️</span>
          <span>글 많이 쓴 사용자 TOP 10</span>
        </h2>
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>💬</span>
          <span>댓글 많이 쓴 사용자 TOP 10</span>
        </h2>
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <span>🗺️</span>
          <span>지역별 관심도 TOP 10</span>
        </h2>
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      </div>
    </div>
  );
}

function SettingsContent() {
  return (
    <div className="bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
        <span>⚙️</span>
        <span>시스템 설정</span>
      </h2>
      <div className="space-y-4">
        <div className="flex items-center justify-between p-4 border rounded-xl">
          <span>신규 회원 가입 허용</span>
          <input type="checkbox" defaultChecked className="w-5 h-5" />
        </div>
        <div className="flex items-center justify-between p-4 border rounded-xl">
          <span>커뮤니티 글 작성 허용</span>
          <input type="checkbox" defaultChecked className="w-5 h-5" />
        </div>
        <div className="flex items-center justify-between p-4 border rounded-xl">
          <span>뉴스 크롤링 활성화</span>
          <input type="checkbox" defaultChecked className="w-5 h-5" />
        </div>
      </div>
    </div>
  );
}
