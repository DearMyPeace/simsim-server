package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.dto.CustomTokenRequestDTO;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.config.auth.dto.TokenForFront;
import com.project.simsim_server.service.user.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<?> googleAuthLogin(@RequestBody CustomTokenRequestDTO requestDTO, HttpServletResponse response) {

        TokenDTO tokenDTO;
        ResponseCookie responseCookie;
        try {
            tokenDTO = authService.login(requestDTO);
            responseCookie = generateRefreshTokenCookie(tokenDTO.getRefreshToken());

        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
        TokenForFront accessToken = TokenForFront.builder()
                .grantType("Bearer")
                .accessToken(tokenDTO.getAccessToken())
                .build();

        return (ResponseEntity<?>) ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(accessToken);
    }


    private static ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("none")
                .build();
    }
}
