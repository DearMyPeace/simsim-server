package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.dto.AppleLoginRequestDTO;
import com.project.simsim_server.config.auth.dto.GoogleLoginRequestDTO;
import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.config.redis.TokenDTO;
import com.project.simsim_server.config.auth.dto.AccessTokenForFrontDTO;
import com.project.simsim_server.exception.CustomRuntimeException;
import com.project.simsim_server.exception.ErrorResponse;
import com.project.simsim_server.exception.auth.AuthErrorCode;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.exception.user.UsersException;
import com.project.simsim_server.service.user.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.simsim_server.exception.auth.AuthErrorCode.
        REFRESH_TOKEN_NOT_EXIST;


@Tag(name = "Auth", description = "소셜 로그인/로그아웃/회원탈퇴 서비스")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthenticationService authenticationService;
    private final Long REFRESH_COOKIE_EXPIRE = 15 * 24 * 60 * 60L;

    /**
     * 구글 로그인
     * @param requestDTO
     * @return ResponseEntity AccessToken, RefreshToken
     */
    @PostMapping("/google")
    public ResponseEntity googleAuthLogin(@RequestBody GoogleLoginRequestDTO requestDTO) throws AuthException {
        log.info("구글 로그인 요청 정보 = {}", requestDTO.toString());

        try {
            TokenDTO tokenDTO = authService.loginGoogle(requestDTO);
            return responseSuccessTokens(tokenDTO);
        } catch (OAuthException | UsersException e) {
            return responseFailTokens(e);
        } catch (Exception e) {
            return responseFailTokens(new OAuthException(AuthErrorCode.LOGIN_FAILED));
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
            return responseSuccessTokens(tokenDTO);
        } catch (OAuthException | UsersException e) {
            return responseFailTokens(e);
        } catch (Exception e) {
            return responseFailTokens(new OAuthException(AuthErrorCode.LOGIN_FAILED));
        }
    }


    /**
     * 회원 로그아웃
     * @param accessToken
     * @return
     */
    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        authService.logout(accessToken);
        return responseElseTokens();
    }


    /**
     * 회원 탈퇴
     * @param accessToken
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity cancleAccount(@RequestHeader("Authorization") String accessToken) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        authService.delete(accessToken, userId);
        return responseElseTokens();
    }


    /**
     * 액세스 토큰 만료 시 토큰 재발급
     * @param requestRefreshToken
     * @return
     */
    @PostMapping("/reissue")
    public ResponseEntity reissueToken(
            @CookieValue(name = "refresh", required = false) String requestRefreshToken) {
        log.warn("---[SimSimLog] RefreshToken = {}", requestRefreshToken);
        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            log.warn("---[SimSimLog] 리프레시 토큰이 존재하지 않아 로그아웃 합니다. ----");
            throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
        }

        try {
            TokenDTO reissuedTokenDto = authService.reissue(requestRefreshToken);
            if (reissuedTokenDto == null) {
                throw new OAuthException(AuthErrorCode.LOGIN_FAILED);
            }
            log.warn("---[SimSimLog] 토큰 재발급 성공 AccessToken = {},  RefreshToken = {}", reissuedTokenDto.getAccessToken(),
                    reissuedTokenDto.getRefreshToken());
            return responseSuccessTokens(reissuedTokenDto);
        } catch (OAuthException | UsersException e) {
            return responseFailTokens(e);
        } catch (Exception e) {
            return responseFailTokens(new OAuthException(AuthErrorCode.LOGIN_FAILED));
        }
    }


    /**
     * 로그인 성공 시 응답
     * @param tokenDTO
     * @return
     */
    private ResponseEntity<AccessTokenForFrontDTO> responseSuccessTokens(TokenDTO tokenDTO) {
        ResponseCookie responseCookie = generateRefreshTokenCookie(tokenDTO.getRefreshToken());

        AccessTokenForFrontDTO accessToken = AccessTokenForFrontDTO.builder()
                .grantType("Bearer")
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(accessToken);
    }

    private ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(REFRESH_COOKIE_EXPIRE)
                .path("/")
                .sameSite("None")
                .build();
    }


    /**
     * 로그인 실패 시 응답
     * @return
     */
    private ResponseEntity<Object> responseFailTokens(CustomRuntimeException e) {
        ResponseCookie responseCookie = deleteRefreshTokenCookie();

        return ResponseEntity.status(e.getErrorType().getCode())
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(new ErrorResponse(e.getErrorType().getMessage(), String.valueOf(e.getErrorType().getCode())));
    }

    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from("refresh", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
    }

    /**
     * 로그아웃 / 회원 탈퇴 시 응답
     * @return
     */
    private ResponseEntity<Object> responseElseTokens() {
        ResponseCookie responseCookie = deleteRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }
}


