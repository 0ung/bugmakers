import logo from "../assets/images/myHome.png";
import kakaoBtn from "../assets/images/kakao.png";
import naverBtn from "../assets/images/naver.png";
import googleBtn from "../assets/images/google.png";

export default function LoginPage() {
  const handleOAuth = (provider: string) => {
    window.location.href = `/oauth2/authorization/${provider}`;
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md bg-white shadow-xl rounded-2xl p-8">
        {/* 로고 */}
        <div className="text-center mb-8">
          <img src={logo} className="h-20 mx-auto mb-4" />
          <h1 className="text-2xl font-bold text-gray-800">마이홈 로그인</h1>
          <p className="text-gray-500 text-sm mt-1">
            소셜 계정으로 간편하게 로그인하세요
          </p>
        </div>

        <div className="space-y-3 flex flex-col items-center">
          {/* Google */}
          <div
            onClick={() => handleOAuth("google")}
            className="cursor-pointer w-[300px] h-[60px] flex items-center justify-center"
          >
            <img src={googleBtn} className="w-full h-full object-contain" />
          </div>

          {/* Kakao */}
          <div
            onClick={() => handleOAuth("kakao")}
            className="cursor-pointer w-[300px] h-[60px] flex items-center justify-center"
          >
            <img src={kakaoBtn} className="w-full h-full object-contain" />
          </div>

          {/* Naver */}
          <div
            onClick={() => handleOAuth("naver")}
            className="cursor-pointer w-[300px] h-[60px] flex items-center justify-center"
          >
            <img src={naverBtn} className="w-full h-full object-contain" />
          </div>
        </div>

        <div className="text-center mt-8 border-t pt-4">
          <p className="text-sm text-gray-500">
            최초 로그인 시 닉네임을 설정합니다.
          </p>
        </div>
      </div>
    </div>
  );
}
