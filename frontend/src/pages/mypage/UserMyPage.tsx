import { useState, useEffect } from "react";
import { useAuthStore } from "../../stores/authStore";
import MainLayout from "../../components/layouts/MainLayout";
import {
  DndContext,
  DragOverlay,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  useDraggable,
  useDroppable,
} from "@dnd-kit/core";

type UserTab = "activity" | "favorites" | "regions" | "settings";

// 위젯 타입 정의
type WidgetType = 
  | "view_news" | "view_trend" | "view_forum"
  | "my_heart_news" | "my_heart_forum" 
  | "others_heart_news" | "others_heart_forum"
  | "my_favorite" | "others_favorite"
  | "my_share" | "others_share"
  | "my_report" | "others_report";

interface Widget {
  id: WidgetType;
  title: string;
  icon: string;
  value: number;
  color: string;
}

// 전체 위젯 목록 (13개)
const ALL_WIDGETS: Widget[] = [
  { id: "view_news", title: "뉴스 조회수", icon: "👀", value: 0, color: "blue" },
  { id: "view_trend", title: "트렌드 조회수", icon: "📊", value: 0, color: "indigo" },
  { id: "view_forum", title: "포럼 조회수", icon: "💬", value: 0, color: "purple" },
  { id: "my_heart_news", title: "내 좋아요 (뉴스)", icon: "❤️", value: 0, color: "red" },
  { id: "my_heart_forum", title: "내 좋아요 (포럼)", icon: "💗", value: 0, color: "pink" },
  { id: "others_heart_news", title: "받은 좋아요 (뉴스)", icon: "👍", value: 0, color: "green" },
  { id: "others_heart_forum", title: "받은 좋아요 (포럼)", icon: "💚", value: 0, color: "teal" },
  { id: "my_favorite", title: "내 즐겨찾기", icon: "⭐", value: 0, color: "yellow" },
  { id: "others_favorite", title: "받은 즐겨찾기", icon: "🌟", value: 0, color: "amber" },
  { id: "my_share", title: "내 공유", icon: "🔗", value: 0, color: "cyan" },
  { id: "others_share", title: "받은 공유", icon: "📤", value: 0, color: "sky" },
  { id: "my_report", title: "내 신고", icon: "🚨", value: 0, color: "orange" },
  { id: "others_report", title: "받은 신고", icon: "⚠️", value: 0, color: "red" },
];

// 기본 위젯 (4개)
const DEFAULT_WIDGETS: WidgetType[] = ["view_news", "my_heart_news", "my_favorite", "my_share"];

// 내 활동 섹션 타입 (17개)
type ActivitySection = 
  | "view_news" | "view_trend" | "view_forum"
  | "my_heart_news" | "my_heart_forum"
  | "others_heart_news" | "others_heart_forum"
  | "my_favorite" | "others_favorite"
  | "my_share" | "others_share"
  | "my_report"
  | "my_posts"
  | "my_comments" | "others_comments"
  | "debates"
  | "regions";

interface ActivitySectionConfig {
  id: ActivitySection;
  title: string;
  icon: string;
  enabled: boolean;
}

// 전체 활동 섹션 (17개)
const ALL_ACTIVITY_SECTIONS: ActivitySectionConfig[] = [
  { id: "view_news", title: "조회한 뉴스", icon: "👀", enabled: false },
  { id: "view_trend", title: "조회한 트렌드", icon: "📊", enabled: false },
  { id: "view_forum", title: "조회한 포럼", icon: "💬", enabled: false },
  { id: "my_heart_news", title: "좋아요한 뉴스", icon: "❤️", enabled: false },
  { id: "my_heart_forum", title: "좋아요한 포럼", icon: "💗", enabled: false },
  { id: "others_heart_news", title: "받은 좋아요 (뉴스)", icon: "👍", enabled: false },
  { id: "others_heart_forum", title: "받은 좋아요 (포럼)", icon: "💚", enabled: false },
  { id: "my_favorite", title: "내 즐겨찾기", icon: "⭐", enabled: false },
  { id: "others_favorite", title: "받은 즐겨찾기", icon: "🌟", enabled: false },
  { id: "my_share", title: "내 공유", icon: "🔗", enabled: false },
  { id: "others_share", title: "받은 공유", icon: "📤", enabled: false },
  { id: "my_report", title: "내 신고", icon: "🚨", enabled: false },
  { id: "my_posts", title: "작성한 글", icon: "✍️", enabled: true },
  { id: "my_comments", title: "작성한 댓글", icon: "💬", enabled: false },
  { id: "others_comments", title: "받은 댓글", icon: "💭", enabled: false },
  { id: "debates", title: "참여 중인 토론", icon: "🔥", enabled: true },
  { id: "regions", title: "자주 보는 지역", icon: "🗺️", enabled: true },
];

export default function UserMyPage() {
  const { user } = useAuthStore();
  const [activeTab, setActiveTab] = useState<UserTab>("activity");
  
  // 위젯 설정
  const [selectedWidgets, setSelectedWidgets] = useState<WidgetType[]>(DEFAULT_WIDGETS);
  
  // 내 활동 섹션 설정
  const [activitySections, setActivitySections] = useState<ActivitySectionConfig[]>(ALL_ACTIVITY_SECTIONS);

  // 설정 하위 탭
  const [settingsTab, setSettingsTab] = useState<"widgets" | "activity" | "all">("widgets");

  // 전체 활동 보기 - 선택된 섹션
  const [selectedAllActivitySection, setSelectedAllActivitySection] = useState<ActivitySection | null>(null);

  // localStorage에서 설정 로드
  useEffect(() => {
    const savedWidgets = localStorage.getItem("userWidgets");
    if (savedWidgets) {
      setSelectedWidgets(JSON.parse(savedWidgets));
    }

    const savedActivitySections = localStorage.getItem("activitySections");
    if (savedActivitySections) {
      setActivitySections(JSON.parse(savedActivitySections));
    }
  }, []);

  // 설정 저장
  const saveWidgetSettings = (widgets: WidgetType[]) => {
    setSelectedWidgets(widgets);
    localStorage.setItem("userWidgets", JSON.stringify(widgets));
  };

  const saveActivitySettings = (sections: ActivitySectionConfig[]) => {
    setActivitySections(sections);
    localStorage.setItem("activitySections", JSON.stringify(sections));
  };

  if (!user) return null;

  // 선택된 위젯 데이터 가져오기
  const displayWidgets = selectedWidgets
    .map(id => ALL_WIDGETS.find(w => w.id === id))
    .filter((w): w is Widget => w !== undefined);

  return (
    <MainLayout>
      <main className="pt-10 pb-16 max-w-7xl mx-auto px-6">
        {/* 프로필 섹션 */}
        <section className="bg-gradient-to-r from-blue-500 to-purple-500 rounded-3xl shadow-2xl p-8 text-white mb-8">
          <div className="flex items-center gap-6">
            <div className="w-24 h-24 bg-white/30 backdrop-blur-sm rounded-full flex items-center justify-center text-5xl border-4 border-white">
              👤
            </div>
            <div className="flex-1">
              <h1 className="text-3xl font-bold mb-2">{user.nickname}님</h1>
              <p className="text-white/80 mb-3">{user.email?.address || "이메일 정보 없음"}</p>
              <div className="flex gap-3">
                <span className="bg-white/30 backdrop-blur-sm px-4 py-1 rounded-full text-sm">
                  👤 일반 회원
                </span>
              </div>
            </div>
            <button className="bg-white text-blue-600 px-6 py-3 rounded-xl font-bold hover:bg-gray-100 transition shadow-lg">
              회원정보 수정
            </button>
          </div>
        </section>

        {/* 위젯 (커스터마이징 가능한 4개 박스) */}
        <section className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          {displayWidgets.map((widget) => (
            <WidgetCard key={widget.id} widget={widget} />
          ))}
        </section>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* 왼쪽: 메뉴 */}
          <aside className="space-y-3">
            <button
              onClick={() => setActiveTab("activity")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "activity"
                  ? "bg-blue-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              📊 내 활동
            </button>
            <button
              onClick={() => setActiveTab("favorites")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "favorites"
                  ? "bg-blue-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              ⭐ 즐겨찾기
            </button>
            <button
              onClick={() => setActiveTab("regions")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "regions"
                  ? "bg-blue-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              🗺️ 관심 지역
            </button>
            <button
              onClick={() => setActiveTab("settings")}
              className={`w-full text-left px-6 py-4 rounded-xl font-medium shadow transition ${
                activeTab === "settings"
                  ? "bg-blue-600 text-white shadow-lg"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              ⚙️ 설정
            </button>
          </aside>

          {/* 오른쪽: 콘텐츠 */}
          <div className="lg:col-span-2">
            {activeTab === "activity" && (
              <ActivityContent sections={activitySections} />
            )}
            {activeTab === "favorites" && <FavoritesContent />}
            {activeTab === "regions" && <RegionsContent />}
            {activeTab === "settings" && (
              <SettingsContent
                settingsTab={settingsTab}
                setSettingsTab={setSettingsTab}
                selectedWidgets={selectedWidgets}
                saveWidgetSettings={saveWidgetSettings}
                activitySections={activitySections}
                saveActivitySettings={saveActivitySettings}
                selectedAllActivitySection={selectedAllActivitySection}
                setSelectedAllActivitySection={setSelectedAllActivitySection}
              />
            )}
          </div>
        </div>
      </main>
    </MainLayout>
  );
}

// 위젯 카드 컴포넌트
function WidgetCard({ widget }: { widget: Widget }) {
  const colorClasses: Record<string, string> = {
    blue: "text-blue-600",
    indigo: "text-indigo-600",
    purple: "text-purple-600",
    red: "text-red-600",
    pink: "text-pink-600",
    green: "text-green-600",
    teal: "text-teal-600",
    yellow: "text-yellow-600",
    amber: "text-amber-600",
    cyan: "text-cyan-600",
    sky: "text-sky-600",
    orange: "text-orange-600",
  };

  return (
    <div className="bg-white rounded-2xl p-6 shadow-lg hover:shadow-xl transition">
      <div className="flex items-center justify-between mb-3">
        <span className="text-gray-600 text-sm">{widget.title}</span>
        <span className="text-3xl">{widget.icon}</span>
      </div>
      <div className={`text-3xl font-bold ${colorClasses[widget.color]}`}>
        {widget.value}
      </div>
    </div>
  );
}

// 탭별 컴포넌트
function ActivityContent({ sections }: { sections: ActivitySectionConfig[] }) {
  const enabledSections = sections.filter(s => s.enabled);

  if (enabledSections.length === 0) {
    return (
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <div className="text-center py-12 text-gray-500">
          활성화된 섹션이 없습니다.<br />
          설정에서 표시할 항목을 선택해주세요.
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {enabledSections.map(section => (
        <ActivitySectionDetail key={section.id} section={section} />
      ))}
    </div>
  );
}

function FavoritesContent() {
  return (
    <ActivitySectionDetail 
      section={{ id: "my_favorite", title: "즐겨찾기 목록", icon: "⭐", enabled: true }}
    />
  );
}

function RegionsContent() {
  return (
    <ActivitySectionDetail 
      section={{ id: "regions", title: "자주 보는 지역", icon: "🗺️", enabled: true }}
    />
  );
}

// ========================================
// 공통 활동 섹션 상세 (리스트 표시)
// ========================================

function ActivitySectionDetail({ section }: { section: ActivitySectionConfig }) {
  // TODO: API 연동으로 실제 데이터 불러오기
  const mockData: any[] = [];

  return (
    <div className="bg-white rounded-2xl shadow-lg p-6">
      <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
        <span>{section.icon}</span>
        <span>{section.title}</span>
      </h2>

      {mockData.length === 0 ? (
        <div className="text-center py-12 text-gray-500">
          데이터가 없습니다.
        </div>
      ) : (
        <div className="space-y-3">
          {mockData.map((item, index) => (
            <div
              key={index}
              className="p-4 border rounded-xl hover:bg-gray-50 transition cursor-pointer"
            >
              <h3 className="font-semibold mb-2">{item.title}</h3>
              <div className="text-sm text-gray-600">{item.description}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

// 설정 컴포넌트
interface SettingsContentProps {
  settingsTab: "widgets" | "activity" | "all";
  setSettingsTab: (tab: "widgets" | "activity" | "all") => void;
  selectedWidgets: WidgetType[];
  saveWidgetSettings: (widgets: WidgetType[]) => void;
  activitySections: ActivitySectionConfig[];
  saveActivitySettings: (sections: ActivitySectionConfig[]) => void;
  selectedAllActivitySection: ActivitySection | null;
  setSelectedAllActivitySection: (section: ActivitySection | null) => void;
}

function SettingsContent(props: SettingsContentProps) {
  return (
    <div className="space-y-6">
      <div className="bg-white rounded-2xl shadow-lg p-6">
        <div className="flex gap-2 mb-6">
          <button
            onClick={() => props.setSettingsTab("widgets")}
            className={`px-4 py-2 rounded-lg font-medium transition ${
              props.settingsTab === "widgets"
                ? "bg-blue-600 text-white"
                : "bg-gray-100 hover:bg-gray-200"
            }`}
          >
            위젯 설정
          </button>
          <button
            onClick={() => props.setSettingsTab("activity")}
            className={`px-4 py-2 rounded-lg font-medium transition ${
              props.settingsTab === "activity"
                ? "bg-blue-600 text-white"
                : "bg-gray-100 hover:bg-gray-200"
            }`}
          >
            내 활동 설정
          </button>
          <button
            onClick={() => props.setSettingsTab("all")}
            className={`px-4 py-2 rounded-lg font-medium transition ${
              props.settingsTab === "all"
                ? "bg-blue-600 text-white"
                : "bg-gray-100 hover:bg-gray-200"
            }`}
          >
            전체 활동 보기
          </button>
        </div>

        {props.settingsTab === "widgets" && (
          <WidgetSettings
            selectedWidgets={props.selectedWidgets}
            saveWidgetSettings={props.saveWidgetSettings}
          />
        )}
        {props.settingsTab === "activity" && (
          <ActivitySettings
            activitySections={props.activitySections}
            saveActivitySettings={props.saveActivitySettings}
          />
        )}
        {props.settingsTab === "all" && (
          <AllActivityView
            selectedSection={props.selectedAllActivitySection}
            setSelectedSection={props.setSelectedAllActivitySection}
          />
        )}
      </div>
    </div>
  );
}

// 위젯 설정 (드래그 앤 드롭)
function WidgetSettings({
  selectedWidgets,
  saveWidgetSettings,
}: {
  selectedWidgets: WidgetType[];
  saveWidgetSettings: (widgets: WidgetType[]) => void;
}) {
  const [tempWidgets, setTempWidgets] = useState<WidgetType[]>(selectedWidgets);
  const [activeId, setActiveId] = useState<string | null>(null);

  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    })
  );

  const handleDragStart = (event: any) => {
    setActiveId(event.active.id as string);
  };

  const handleDragEnd = (event: any) => {
    const { active, over } = event;
    setActiveId(null);

    if (!over) return;

    const activeId = active.id as string;
    const overId = over.id as string;

    // 슬롯에 드롭
    if (overId.startsWith("slot-")) {
      const slotIndex = parseInt(overId.split("-")[1]);
      const newWidgets = [...tempWidgets];
      
      // 이미 선택된 위젯이면 순서 변경
      const existingIndex = newWidgets.findIndex(w => w === activeId);
      if (existingIndex !== -1) {
        // 같은 위치면 무시
        if (existingIndex === slotIndex) return;
        
        // 순서 변경
        newWidgets.splice(existingIndex, 1);
        newWidgets.splice(slotIndex, 0, activeId as WidgetType);
      } else {
        // 새로 추가 (4개 제한, 초과시 마지막 제거)
        if (newWidgets.length >= 4) {
          newWidgets.pop(); // 마지막 제거
        }
        newWidgets.splice(slotIndex, 0, activeId as WidgetType);
      }
      
      // 4개로 제한
      if (newWidgets.length > 4) {
        newWidgets.length = 4;
      }
      
      setTempWidgets(newWidgets);
    }
  };

  const handleRemove = (index: number) => {
    const newWidgets = [...tempWidgets];
    newWidgets.splice(index, 1);
    setTempWidgets(newWidgets);
  };

  const handleSave = () => {
    if (tempWidgets.length !== 4) {
      alert("정확히 4개를 선택해주세요.");
      return;
    }
    saveWidgetSettings(tempWidgets);
    alert("위젯 설정이 저장되었습니다!");
  };

  const activeWidget = activeId ? ALL_WIDGETS.find(w => w.id === activeId) : null;

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
    >
      <div>
        <h3 className="font-bold text-lg mb-4">
          상단 위젯 설정 (드래그하여 배치) - {tempWidgets.length}/4
        </h3>
        <p className="text-sm text-gray-600 mb-4">
          💡 하단 위젯을 위로 드래그하세요. 4개 초과시 자동으로 마지막 위젯이 제거됩니다.
        </p>

        {/* 상단 4개 슬롯 */}
        <div className="grid grid-cols-4 gap-4 mb-6 p-4 bg-blue-50 rounded-xl">
          {[0, 1, 2, 3].map((slotIndex) => (
            <WidgetSlot
              key={`slot-${slotIndex}`}
              id={`slot-${slotIndex}`}
              widget={tempWidgets[slotIndex] ? ALL_WIDGETS.find(w => w.id === tempWidgets[slotIndex]) : undefined}
              onRemove={() => handleRemove(slotIndex)}
            />
          ))}
        </div>

        {/* 전체 위젯 목록 */}
        <h4 className="font-semibold mb-3">위젯 목록 (드래그하여 위로 이동)</h4>
        <div className="grid grid-cols-3 gap-3 mb-4">
          {ALL_WIDGETS.map((widget) => (
            <DraggableWidget key={widget.id} widget={widget} />
          ))}
        </div>

        <button
          onClick={handleSave}
          className="w-full py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition"
        >
          저장하기
        </button>
      </div>

      {/* Drag Overlay */}
      <DragOverlay>
        {activeWidget ? (
          <div className="p-4 bg-white rounded-xl shadow-2xl border-2 border-blue-500">
            <div className="text-center">
              <div className="text-2xl mb-1">{activeWidget.icon}</div>
              <div className="text-xs font-medium">{activeWidget.title}</div>
            </div>
          </div>
        ) : null}
      </DragOverlay>
    </DndContext>
  );
}

// 드래그 가능한 위젯
function DraggableWidget({ widget }: { widget: Widget }) {
  const { attributes, listeners, setNodeRef, transform, isDragging } = useDraggable({
    id: widget.id,
  });

  const style = transform ? {
    transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
    opacity: isDragging ? 0.5 : 1,
  } : undefined;

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className="p-3 rounded-xl border-2 border-gray-200 hover:border-blue-400 cursor-move transition bg-white"
    >
      <div className="text-center">
        <div className="text-2xl mb-1">{widget.icon}</div>
        <div className="text-xs font-medium text-gray-700">{widget.title}</div>
      </div>
    </div>
  );
}

// 위젯 슬롯
function WidgetSlot({ id, widget, onRemove }: { id: string; widget?: Widget; onRemove: () => void }) {
  const { setNodeRef, isOver } = useDroppable({ id });

  return (
    <div
      ref={setNodeRef}
      className={`relative p-4 rounded-xl border-2 border-dashed min-h-[100px] flex items-center justify-center transition ${
        isOver ? "border-blue-500 bg-blue-100" : "border-gray-300 bg-gray-50"
      }`}
    >
      {widget ? (
        <div className="text-center">
          <button
            onClick={onRemove}
            className="absolute top-1 right-1 w-5 h-5 bg-red-500 text-white rounded-full text-xs hover:bg-red-600 flex items-center justify-center"
          >
            ×
          </button>
          <div className="text-2xl mb-1">{widget.icon}</div>
          <div className="text-xs font-medium">{widget.title}</div>
        </div>
      ) : (
        <div className="text-gray-400 text-xs">드롭하세요</div>
      )}
    </div>
  );
}

// 내 활동 설정
function ActivitySettings({
  activitySections,
  saveActivitySettings,
}: {
  activitySections: ActivitySectionConfig[];
  saveActivitySettings: (sections: ActivitySectionConfig[]) => void;
}) {
  const enabledSections = activitySections.filter(s => s.enabled);
  const disabledSections = activitySections.filter(s => !s.enabled);

  const [tempEnabled, setTempEnabled] = useState<ActivitySectionConfig[]>(enabledSections);
  const [tempDisabled, setTempDisabled] = useState<ActivitySectionConfig[]>(disabledSections);

  const handleToggle = (section: ActivitySectionConfig) => {
    if (section.enabled) {
      setTempEnabled(tempEnabled.filter(s => s.id !== section.id));
      setTempDisabled([...tempDisabled, { ...section, enabled: false }]);
    } else {
      setTempDisabled(tempDisabled.filter(s => s.id !== section.id));
      setTempEnabled([...tempEnabled, { ...section, enabled: true }]);
    }
  };

  const moveUp = (index: number) => {
    if (index === 0) return;
    const sections = [...tempEnabled];
    [sections[index - 1], sections[index]] = [sections[index], sections[index - 1]];
    setTempEnabled(sections);
  };

  const moveDown = (index: number) => {
    if (index === tempEnabled.length - 1) return;
    const sections = [...tempEnabled];
    [sections[index], sections[index + 1]] = [sections[index + 1], sections[index]];
    setTempEnabled(sections);
  };

  const handleSave = () => {
    const combined = [...tempEnabled, ...tempDisabled];
    saveActivitySettings(combined);
    alert("내 활동 설정이 저장되었습니다!");
  };

  return (
    <div>
      <h3 className="font-bold text-lg mb-4">
        내 활동 섹션 설정 (활성화: {tempEnabled.length}개)
      </h3>

      {/* 활성화된 섹션 */}
      {tempEnabled.length > 0 && (
        <div className="mb-6">
          <h4 className="font-semibold mb-3 text-blue-600">✅ 활성화된 섹션</h4>
          <div className="space-y-3">
            {tempEnabled.map((section, index) => (
              <div
                key={section.id}
                className="flex items-center gap-3 p-4 border-2 border-blue-300 bg-blue-50 rounded-xl"
              >
                <input
                  type="checkbox"
                  checked={true}
                  onChange={() => handleToggle(section)}
                  className="w-5 h-5"
                />
                <span className="text-2xl">{section.icon}</span>
                <span className="flex-1 font-medium">{section.title}</span>
                <div className="flex gap-2">
                  <button
                    onClick={() => moveUp(index)}
                    disabled={index === 0}
                    className="px-3 py-1 bg-white rounded hover:bg-gray-100 disabled:opacity-30"
                  >
                    ↑
                  </button>
                  <button
                    onClick={() => moveDown(index)}
                    disabled={index === tempEnabled.length - 1}
                    className="px-3 py-1 bg-white rounded hover:bg-gray-100 disabled:opacity-30"
                  >
                    ↓
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 비활성화된 섹션 */}
      <div>
        <h4 className="font-semibold mb-3 text-gray-600">⬜ 비활성화된 섹션</h4>
        <div className="space-y-3">
          {tempDisabled.map((section) => (
            <div
              key={section.id}
              className="flex items-center gap-3 p-4 border-2 border-gray-200 rounded-xl"
            >
              <input
                type="checkbox"
                checked={false}
                onChange={() => handleToggle(section)}
                className="w-5 h-5"
              />
              <span className="text-2xl">{section.icon}</span>
              <span className="flex-1 font-medium text-gray-600">{section.title}</span>
            </div>
          ))}
        </div>
      </div>

      <button
        onClick={handleSave}
        className="w-full py-3 mt-6 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition"
      >
        저장하기
      </button>
    </div>
  );
}

// 전체 활동 보기
function AllActivityView({
  selectedSection,
  setSelectedSection,
}: {
  selectedSection: ActivitySection | null;
  setSelectedSection: (section: ActivitySection | null) => void;
}) {
  if (selectedSection) {
    const section = ALL_ACTIVITY_SECTIONS.find(s => s.id === selectedSection);
    if (!section) return null;

    return (
      <div>
        <button
          onClick={() => setSelectedSection(null)}
          className="mb-4 text-blue-600 hover:underline flex items-center gap-2"
        >
          ← 목록으로 돌아가기
        </button>
        <ActivitySectionDetail section={section} />
      </div>
    );
  }

  return (
    <div>
      <h3 className="font-bold text-lg mb-4">전체 활동 내역</h3>
      <div className="space-y-3">
        {ALL_ACTIVITY_SECTIONS.map((section) => (
          <button
            key={section.id}
            onClick={() => setSelectedSection(section.id)}
            className="w-full flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition text-left"
          >
            <div className="flex items-center gap-3">
              <span className="text-2xl">{section.icon}</span>
              <span className="font-medium">{section.title}</span>
            </div>
            <div className="flex items-center gap-3">
              <span className="text-xl font-bold text-blue-600">0</span>
              <span className="text-gray-400">→</span>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
