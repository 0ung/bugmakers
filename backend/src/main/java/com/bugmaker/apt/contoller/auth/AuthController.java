package com.bugmaker.apt.contoller.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        deleteCookie(response, "accessToken", false);
        deleteCookie(response, "refreshToken", true);
        return ResponseEntity.ok().build();
    }

    private void deleteCookie(HttpServletResponse response, String name, boolean httpOnly) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
