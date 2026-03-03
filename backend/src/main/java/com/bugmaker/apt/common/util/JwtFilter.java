package com.bugmaker.apt.common.util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final String tokenHeader = "accessToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("jwt filter started");
            String jwtToken = parseJwt(request);
            log.debug("jwt Token : {}", jwtToken);
            if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
                Authentication authentication = jwtUtil.getAuthentication(jwtToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.info("jwt error : {}", e.getMessage());
        }

        doFilter(request, response, filterChain);
    }

    private String parseJwt(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (int i = 0; i < request.getCookies().length; i++) {
            Cookie cookie = request.getCookies()[i];
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(tokenHeader))
                .map(Cookie::getValue)
                .filter(jwtUtil::validateToken)
                .findFirst()
                .orElse(null);
    }

}
