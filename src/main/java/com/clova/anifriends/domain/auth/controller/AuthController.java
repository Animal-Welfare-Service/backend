package com.clova.anifriends.domain.auth.controller;

import com.clova.anifriends.domain.auth.dto.request.LoginRequest;
import com.clova.anifriends.domain.auth.dto.response.LoginResponse;
import com.clova.anifriends.domain.auth.dto.response.TokenResponse;
import com.clova.anifriends.domain.auth.exception.AuthAuthenticationException;
import com.clova.anifriends.domain.auth.service.AuthService;
import com.clova.anifriends.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    private final AuthService authService;

    @PostMapping("/volunteers/login")
    public ResponseEntity<LoginResponse> volunteerLogin(
        @RequestBody @Valid LoginRequest loginRequest,
        HttpServletResponse response) {
        TokenResponse tokenResponse = authService.volunteerLogin(
            loginRequest.email(),
            loginRequest.password(),
            loginRequest.deviceToken());
        addRefreshTokenCookie(response, tokenResponse);
        LoginResponse loginResponse = LoginResponse.from(tokenResponse);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/shelters/login")
    public ResponseEntity<LoginResponse> shelterLogin(
        @RequestBody @Valid LoginRequest loginRequest,
        HttpServletResponse response) {
        TokenResponse tokenResponse = authService.shelterLogin(
            loginRequest.email(),
            loginRequest.password(),
            loginRequest.deviceToken());
        addRefreshTokenCookie(response, tokenResponse);
        LoginResponse loginResponse = LoginResponse.from(tokenResponse);
        return ResponseEntity.ok(loginResponse);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie refreshTokenCookie = ResponseCookie
            .from(REFRESH_TOKEN_COOKIE, tokenResponse.refreshToken())
            .path("/api/auth")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .domain(".anifriends.site")
            .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshAccessToken(
        @CookieValue(value = REFRESH_TOKEN_COOKIE, required = false) String refreshToken,
        HttpServletResponse response) {
        if(Objects.isNull(refreshToken)) {
            throw new AuthAuthenticationException(ErrorCode.NOT_EXISTS_REFRESH_TOKEN,
                "리프레시 토큰이 존재하지 않습니다.");
        }
        TokenResponse tokenResponse = authService.refreshAccessToken(refreshToken);
        addRefreshTokenCookie(response, tokenResponse);
        LoginResponse loginResponse = LoginResponse.from(tokenResponse);
        return ResponseEntity.ok(loginResponse);
    }
}
