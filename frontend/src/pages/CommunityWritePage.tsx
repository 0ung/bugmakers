import { useState } from "react";
import MainLayout from "../components/layouts/MainLayout";

export default function CommunityWritePage() {
  const [options, setOptions] = useState(["", ""]); // 기본 2개 항목

  const addOption = () => {
    if (options.length < 5) setOptions([...options, ""]);
  };

  return (
    <MainLayout>
      <form className="max-w-4xl mx-auto bg-white p-8 rounded-2xl shadow space-y-6">
        <h2 className="text-2xl font-bold">토론 주제 작성</h2>

        <div>
          <label className="block text-sm font-medium mb-2">제목 *</label>
          <input
            className="w-full border p-3 rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
            placeholder="주제를 입력하세요"
          />
        </div>

        <div>
          <label className="block text-sm font-medium mb-2">
            투표 항목 * (최대 5개)
          </label>
          <div className="space-y-3">
            {options.map((opt, idx) => (
              <input
                key={idx}
                className="w-full border p-3 rounded-lg"
                placeholder={`항목 ${idx + 1}`}
                value={opt}
                onChange={(e) => {
                  const newOpts = [...options];
                  newOpts[idx] = e.target.value;
                  setOptions(newOpts);
                }}
              />
            ))}
          </div>
          {options.length < 5 && (
            <button
              type="button"
              onClick={addOption}
              className="mt-3 text-blue-600 font-medium"
            >
              + 항목 추가
            </button>
          )}
        </div>

        <button className="w-full bg-blue-600 text-white py-4 rounded-xl font-bold">
          등록하기
        </button>
      </form>
    </MainLayout>
  );
}
