import logo from "../assets/images/myHome.png";

export default function LoginPage() {
  const handleOAuth = (provider: string) => {
    const baseUrl = import.meta.env.VITE_API_BASE_URL;
    window.location.href = `${baseUrl}/oauth2/authorization/${provider}`;
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="w-full max-w-md bg-white shadow-2xl rounded-[20px] p-10">
        {/* 로고 섹션 */}
        <div className="text-center mb-10">
          <img src={logo} alt="MyHome Logo" className="h-20 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
            마이홈 로그인
          </h1>
          <p className="text-gray-500 text-base mt-2">
            소셜 계정으로 간편하게 시작하세요
          </p>
        </div>

        {/* 로그인 버튼 리스트 */}
        <div className="space-y-4 flex flex-col items-center w-full">
          {/* Google 버튼 */}
          <button
            onClick={() => handleOAuth("google")}
            className="w-full max-w-[320px] h-[55px] flex items-center justify-center gap-3 bg-white border-2 border-gray-200 rounded-xl text-gray-700 font-semibold hover:bg-gray-50 hover:-translate-y-0.5 transition-all duration-200 shadow-sm"
          >
            <svg className="w-6 h-6" viewBox="0 0 48 48">
              <path
                fill="#4285F4"
                d="M45.12 24.5c0-1.56-.14-3.06-.4-4.5H24v8.51h11.84c-.51 2.75-2.06 5.08-4.39 6.64v5.52h7.11c4.16-3.83 6.56-9.47 6.56-16.17z"
              />
              <path
                fill="#34A853"
                d="M24 46c5.94 0 10.92-1.97 14.56-5.33l-7.11-5.52c-1.97 1.32-4.49 2.1-7.45 2.1-5.73 0-10.58-3.87-12.31-9.07H4.34v5.7C7.96 41.07 15.4 46 24 46z"
              />
              <path
                fill="#FBBC05"
                d="M11.69 28.18C11.25 26.86 11 25.45 11 24s.25-2.86.69-4.18v-5.7H4.34C2.85 17.09 2 20.45 2 24c0 3.55.85 6.91 2.34 9.88l7.35-5.7z"
              />
              <path
                fill="#EA4335"
                d="M24 10.75c3.23 0 6.13 1.11 8.41 3.29l6.31-6.31C34.91 4.18 29.93 2 24 2 15.4 2 7.96 6.93 4.34 14.12l7.35 5.7c1.73-5.2 6.58-9.07 12.31-9.07z"
              />
            </svg>
            Google로 계속하기
          </button>

          {/* Naver 버튼 */}
          <button
            onClick={() => handleOAuth("naver")}
            className="w-full max-w-[320px] h-[55px] flex items-center justify-center gap-3 bg-[#03C75A] rounded-xl text-white font-semibold hover:bg-[#02b350] hover:-translate-y-0.5 transition-all duration-200 shadow-sm"
          >
            <span className="font-black text-xl italic mr-1">N</span>
            네이버로 계속하기
          </button>

          {/* Kakao 버튼 */}
          <button
            onClick={() => handleOAuth("kakao")}
            className="w-full max-w-[320px] h-[55px] flex items-center justify-center gap-3 bg-[#FEE500] rounded-xl text-[#191919] font-semibold hover:bg-[#fdd800] hover:-translate-y-0.5 transition-all duration-200 shadow-sm"
          >
            <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 3c5.799 0 10.5 3.664 10.5 8.185 0 4.52-4.701 8.184-10.5 8.184a13.5 13.5 0 0 1-1.727-.11l-4.408 2.883c-.501.265-.678.236-.472-.413l.892-3.678c-2.88-1.46-4.785-3.99-4.785-6.866C1.5 6.665 6.201 3 12 3z" />
            </svg>
            카카오로 계속하기
          </button>
        </div>

        {/* 하단 안내 문구 */}
        <div className="mt-10 pt-6 border-t border-gray-100 text-center text-sm text-gray-500">
          <p>최초 로그인 시 닉네임을 설정합니다.</p>
        </div>
      </div>
    </div>
  );
}
