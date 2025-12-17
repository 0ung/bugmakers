package com.bugmaker.apt.service.member.oauth;

import com.bugmaker.apt.common.util.JwtUtil;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.member.oauth.CustomOAuth2User;
import com.bugmaker.apt.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 프론트엔드로 리다이렉트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    @Value("${redirect.HOST_DOMAIN}")
    private String domain;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long memberId = oAuth2User.getMemberId();

        log.info("OAuth2 로그인 성공 - MemberId: {}", memberId);

        // JWT 토큰 생성
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));

        log.info("OAuth2 조회 성공 - MemberId: {}", memberId);

        String accessToken = jwtUtil.generateAccessToken(memberId, member.getEmail().toString());
        String refreshToken = jwtUtil.generateRefreshToken(memberId, member.getEmail().toString());
        member.updateRefreshToken(refreshToken);

        // 3. 쿠키 생성 및 추가 (URL 파라미터 X)
        // AccessToken: 프론트에서 JS로 읽어야 한다면 httpOnly(false)
        addCookie(response, "accessToken", accessToken, 60 * 60, false);

        // RefreshToken: 보통 보안상 httpOnly(true)를 쓰지만, 프론트 로직에 따라 false로 변경 가능
        addCookie(response, "refreshToken", refreshToken, 14 * 24 * 60 * 60, true);

        // 4. 메인으로 깔끔하게 리다이렉트
        String targetUrl = domain + "/";

        log.info("Redirecting to Main (Cookies set): {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");       // 모든 경로에서 접근 가능
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);  // 초 단위 설정
        // cookie.setSecure(true); // HTTPS 환경이면 주석 해제

        response.addCookie(cookie);
    }
}
