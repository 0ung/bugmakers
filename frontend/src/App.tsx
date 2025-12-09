function App() {
  return (
    // 배경을 화려한 그라데이션으로 해서 배포된 걸 바로 알 수 있게 함
    <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 text-white">
      {/* 메인 박스 */}
      <div className="bg-white/20 backdrop-blur-lg rounded-2xl p-10 shadow-2xl border border-white/30 text-center max-w-lg">
        <h1 className="text-5xl font-extrabold mb-4 drop-shadow-md">
          CI/CD 테스트 중! 🚀
        </h1>

        <div className="text-xl font-medium mb-8 bg-black/20 rounded-lg py-2 px-4 inline-block">
          현재 버전: <span className="text-yellow-300">v1.0.0 (Dev)</span>
        </div>

        <p className="text-lg mb-6 leading-relaxed opacity-90">
          이 화면이 보인다면 깃허브 액션이
          <br />
          정상적으로 작동해서 배포된 것입니다.
        </p>

        <button
          onClick={() => alert("자바스크립트도 잘 먹힙니다!")}
          className="bg-white text-indigo-600 font-bold py-3 px-8 rounded-full shadow-lg hover:bg-yellow-300 hover:text-indigo-800 transition-all transform hover:scale-105"
        >
          클릭 테스트
        </button>
      </div>

      <footer className="absolute bottom-4 text-white/50 text-sm">
        Dev Server Environment Dev Server Environment Dev Server Environment
      </footer>
    </div>
  );
}

export default App;
