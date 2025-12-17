import { useCallback } from "react";

export const useOAuthLogin = () => {
  const loginWithGoogle = useCallback(() => {
    window.location.href = "/oauth2/authorization/google";
  }, []);

  const loginWithKakao = useCallback(() => {
    window.location.href = "/oauth2/authorization/kakao";
  }, []);

  const loginWithNaver = useCallback(() => {
    window.location.href = "/oauth2/authorization/naver";
  }, []);

  return {
    loginWithGoogle,
    loginWithKakao,
    loginWithNaver,
  };
};
