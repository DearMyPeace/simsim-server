package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.dto.CustomTokenRequestDTO;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.config.auth.dto.TokenForFront;
import com.project.simsim_server.service.user.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.data.redis.connection.ReactiveStreamCommands.AddStreamRecord.body;

@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    @Value("${spring.jwt.access.expiration}")
    private Long accessExpireTime;
    @Value("${spring.jwt.refresh.expiration}")
    private Long refreshExpireTime;
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



//1.예제 - ChatGPT
//public class AuthController {
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    private UsersRepository usersRepository;
//    private DiaryRepository diaryRepository;
//    private JwtUtils jwtUtils;
//
//    @PostMapping("/google")
//    public String googleLogin(@RequestBody CustomTokenRequestDTO tokenRequest,
//                              HttpServletResponse response) throws GeneralSecurityException, IOException {
//
//        log.info("GOOGLE LOGIN REQUEST: {}", tokenRequest);
//
//        /**
//         * 구글 로그인 토큰 검증
//         */
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                new NetHttpTransport(), new JacksonFactory())
//                .setAudience(Collections.singletonList(googleClientId))
//                .build();
//
//        GoogleIdToken idToken = verifier.verify(tokenRequest.getAccess_token());
//        if (idToken == null) {
//            throw new IllegalArgumentException("Invalid ID token.");
//        }
//        GoogleIdToken.Payload payload = idToken.getPayload();
//        String userName = payload.getSubject();
//        String userEmail = payload.getEmail();
//
//
//        /**
//         * DB 회원 정보 유무 확인
//         * - 회원 정보가 있으면 이름만 변경
//         * - 회원 정보가 없으면 새로 DB에 등록
//         */
//        Users user = usersRepository.findByEmail(userEmail)
//                .map(entity -> entity.update(userName))
//                .orElse(Users.builder()
//                        .name(userName)
//                        .email(userEmail)
//                        .role(Role.USER)
//                        .build()
//                );
//        Users savedUser = usersRepository.save(user);
//
//        // 하단 편지 메뉴 표시 상태
//        Reply replyStatus = Reply.EMPTY;
//        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
//        List<Diary> diaries = diaryRepository.findDiariesByCreatedDateAndUserId(yesterday,
//                savedUser.getUserId());
//        if (!diaries.isEmpty()) {
//            replyStatus = Reply.OCCUPIED;
//        }
//        UserTokenInfo userTokenInfo
//                = UserTokenInfo.fromUser(savedUser, replyStatus.getKey());
//
//        /**
//         * JWT 발급 및 Cookie에 저장(+ 편지 표시 상태)
//         */
//        String accessToken = jwtUtils.generateAccessToken(userTokenInfo);
//        String refreshToken = jwtUtils.generateRefreshToken(userTokenInfo);
//
//        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//        accessTokenCookie.setHttpOnly(true);
//        accessTokenCookie.setPath("/");
//        accessTokenCookie.setMaxAge((int) (jwtUtils.getAccessExpireTime() / 1000)); // seconds
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setMaxAge((int) (jwtUtils.getRefreshExpireTime() / 1000)); // seconds
//
//        response.addCookie(accessTokenCookie);
//        response.addCookie(refreshTokenCookie);
//
//        return "Login successful";
//    }
//}
