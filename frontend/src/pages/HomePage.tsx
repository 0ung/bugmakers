// src/pages/HomePage.tsx
import MainLayout from "../components/layouts/MainLayout";

export default function HomePage() {
  return (
    <MainLayout>
      <h1 className="text-2xl font-bold mb-4">마이홈 대시보드</h1>
      <p className="text-gray-600">
        여기다가 home.html에 있던 차트/위젯들을 React로 옮겨오면 됨.
      </p>
    </MainLayout>
  );
}
