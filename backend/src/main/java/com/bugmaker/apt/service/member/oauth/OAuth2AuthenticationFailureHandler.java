package com.bugmaker.apt.service.member.oauth;

import com.bugmaker.apt.common.util.JwtUtil;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.member.oauth.CustomOAuth2User;
import com.bugmaker.apt.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 로그인 실패 시 처리
 * 에러 로그를 남기고 /login 페이지로 리다이렉트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${redirect.HOST_DOMAIN}") // 예: http://localhost:3000
    private String domain;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 1. 에러 로그 출력 (서버 확인용)
        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        // 2. 리다이렉트 URL 생성
        // 실패했으므로 다시 로그인 페이지로 보내되, 에러가 났다는 표시를 쿼리 파라미터로 남김
        String targetUrl = UriComponentsBuilder.fromUriString(domain)
                .path("/login")
                .queryParam("error", "oauth_failed")
                .build().toUriString();

        log.info("Redirecting to Login (Failure): {}", targetUrl);

        // 3. 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
