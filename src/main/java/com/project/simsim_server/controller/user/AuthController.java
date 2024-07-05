package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.dto.AppleLoginRequestDTO;
import com.project.simsim_server.config.auth.dto.GoogleLoginRequestDTO;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.config.auth.dto.AccessTokenForFrontDTO;
import com.project.simsim_server.exception.ErrorResponse;
import com.project.simsim_server.exception.UserNotFoundException;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.service.auth.AuthService;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static com.project.simsim_server.exception.auth.AuthErrorCode.REFRESH_TOKEN_NOT_EXIST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    /**
     * 구글 로그인
     *
     * @param requestDTO
     * @return ResponseEntity AccessToken, RefreshToken
     */
    @PostMapping("/google")
    public ResponseEntity googleAuthLogin(@RequestBody GoogleLoginRequestDTO requestDTO) throws AuthException {
        try {
            TokenDTO tokenDTO = authService.loginGoogle(requestDTO);
            ResponseCookie responseCookie = generateRefreshTokenCookie(tokenDTO.getRefreshToken());

            AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                    .grantType("Bearer")
                    .accessToken(tokenDTO.getAccessToken())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(accessToken);
        } catch (UserNotFoundException ex) {
            return handleUserNotFoundException(ex);
        }
    }

    /**
     * 애플 로그인
     * @param requestDTO
     * @return
     * @throws AuthException
     */
    @PostMapping("/apple")
    public ResponseEntity appleAuthLogin(@RequestBody AppleLoginRequestDTO requestDTO) throws AuthException {
        log.info("애플 로그인 요청 정보 = {}", requestDTO.toString());

        try {
            TokenDTO tokenDTO = authService.loginApple(requestDTO);
            ResponseCookie responseCookie = generateRefreshTokenCookie(tokenDTO.getRefreshToken());

            AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                    .grantType("Bearer")
                    .accessToken(tokenDTO.getAccessToken())
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(accessToken);
        } catch (UserNotFoundException ex) {
            return handleUserNotFoundException(ex);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
                .header(HttpHeaders.LOCATION, "/")
                .build();
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
                .body(authentication);
    }


    @PostMapping("/reissue")
    public ResponseEntity reissueToken(
            @CookieValue(name = "refresh", required = false) String requestRefreshToken) {

        if (requestRefreshToken.isEmpty()) {
            log.warn("---[SimSimLog] 리프레시 토큰이 존재하지 않아 로그아웃 합니다. ----");
            throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
        }

        log.warn("---[SimSimLog] RefreshToken = {}", requestRefreshToken);

        TokenDTO reissuedTokenDto = authService.reissue(requestRefreshToken);
        if (reissuedTokenDto != null) {
            ResponseCookie responseCookie = generateRefreshTokenCookie(reissuedTokenDto.getRefreshToken());

            AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                    .grantType("Bearer")
                    .accessToken(reissuedTokenDto.getAccessToken())
                    .build();
            log.warn("---[SimSimLog] 새로 발급한 토큰 AccessToken = {},  RefreshToken = {}", reissuedTokenDto.getAccessToken(),
                    reissuedTokenDto.getRefreshToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(accessToken);
        } else {

            log.warn("---[SimSimLog] 토큰 발급 실패");

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
//        Long REFRESH_COOKIE_EXPIRE = 7 * 24 * 60 * 60L;
        Long REFRESH_COOKIE_EXPIRE = 32700L;
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(REFRESH_COOKIE_EXPIRE)
                .path("/")
                .sameSite("None")
                .build();
    }

    private String getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        ResponseCookie responseCookie = ResponseCookie.from("refresh", "")
                .maxAge(0)
                .path("/")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(errorResponse);
    }
}

