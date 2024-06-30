package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.dto.CustomTokenRequestDTO;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.config.auth.dto.AccessTokenForFrontDTO;
import com.project.simsim_server.service.user.AuthService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final Long REFRESH_COOKIE_EXPIRE = 7 * 24 * 60 * 60L;

    /**
     * 구글 로그인
     *
     * @param requestDTO
     * @return ResponseEntity AccessToken, RefreshToken
     */
    @PostMapping("/google")
    public ResponseEntity googleAuthLogin(@RequestBody CustomTokenRequestDTO requestDTO) throws AuthException {

        TokenDTO tokenDTO = authService.login(requestDTO);
        ResponseCookie responseCookie = generateRefreshTokenCookie(tokenDTO.getRefreshToken());

        AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                .grantType("Bearer")
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(accessToken);
    }

    /**
     * 회원 로그아웃
     * @param accessToken
     * @return
     */
    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        authService.logout(accessToken, userId);
        ResponseCookie responseCookie = ResponseCookie.from("refresh", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }


    /**
     * 회원 탈퇴
     * @param accessToken
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity cancleAccount(@RequestHeader("Authorization") String accessToken) {
        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        authService.delete(accessToken, userId);
        ResponseCookie responseCookie = ResponseCookie.from("refresh", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }


    @PostMapping("/reissue")
    public ResponseEntity reissueToken(
            @CookieValue(name = "refresh") String requestRefreshToken,
            @RequestHeader("Authorization") String requestAccessToken) {
        TokenDTO reissuedTokenDto = authService.reissue(requestRefreshToken, requestAccessToken);
        if (reissuedTokenDto != null) {
            ResponseCookie responseCookie = generateRefreshTokenCookie(reissuedTokenDto.getRefreshToken());

            AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                    .grantType("Bearer")
                    .accessToken(reissuedTokenDto.getAccessToken())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(accessToken);
        } else {
            ResponseCookie responseCookie = ResponseCookie.from("refresh", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(null);
        }
    }

    private ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(false)
                .maxAge(REFRESH_COOKIE_EXPIRE)
                .path("/")
                .sameSite("None")
                .build();
    }

    private String getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
