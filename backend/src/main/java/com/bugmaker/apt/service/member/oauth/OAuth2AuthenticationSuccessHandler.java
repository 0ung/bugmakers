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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long memberId = oAuth2User.getMemberId();

        log.info("OAuth2 로그인 성공 - MemberId: {}", memberId);

        // JWT 토큰 생성
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));

        // Email이 record이므로 address() 메서드 사용
        String token = jwtUtil.generateToken(member.getId(), member.getEmail().address());

        // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080")
                .queryParam("token", token)
                .build().toUriString();

        log.info("JWT 토큰 생성 완료. 리다이렉트: {}", targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
