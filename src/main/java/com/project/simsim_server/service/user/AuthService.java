package com.project.simsim_server.service.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.project.simsim_server.config.auth.dto.*;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Reply;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import jakarta.security.auth.message.AuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Transactional
@Service
public class AuthService {
    private final GoogleIdTokenVerifier verifier;
    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;
    private final DiaryRepository diaryRepository;
    private final RestTemplate restTemplate;
    private final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/userinfo/v2/me";

    public AuthService(
            @Value("${spring.security.oauth2.client.registration.google.client-id}") String clientId,
            JwtUtils jwtUtils,
            UsersRepository usersRepository,
            DiaryRepository diaryRepository,
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singleton(clientId))
                .build();
        this.jwtUtils = jwtUtils;
        this.usersRepository = usersRepository;
        this.diaryRepository = diaryRepository;
    }

    public TokenDTO login(CustomTokenRequestDTO tokenRequest) throws AuthException {

        /**
         * 구글 로그인 토큰 검증
         */
        try {
//            GoogleIdToken idToken = verifier.verify(tokenRequest.getAccess_token());
//            if(idToken == null) {
//                throw new IllegalArgumentException("IdToken is Empty.");
//            }
//            GoogleIdToken.Payload payload = idToken.getPayload();
//            String userName = payload.getSubject();
//            String userEmail = payload.getEmail();
            GoogleUserInfo userInfo = getGoogleUserInfo(tokenRequest.getAccess_token());
            String userEmail = userInfo.getEmail();
            String userName = userInfo.getName();

            /**
             * DB 회원 정보 유무 확인
             * - 회원 정보가 있으면 이름만 변경
             * - 회원 정보가 없으면 새로 DB에 등록
             */
            Users user = usersRepository.findByEmail(userEmail)
                    .map(entity -> entity.update(userName))
                    .orElse(Users.builder()
                            .name(userName)
                            .email(userEmail)
                            .role(Role.USER)
                            .build()
                    );
            Users savedUser = usersRepository.save(user);
            // 하단 편지 메뉴 표시 상태
            Reply replyStatus = Reply.EMPTY;
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            List<Diary> diaries = diaryRepository.findDiariesByCreatedDateAndUserId(yesterday,
                    savedUser.getUserId());
            if (!diaries.isEmpty()) {
                replyStatus = Reply.OCCUPIED;
            }
            UserTokenInfo userTokenInfo
                    = UserTokenInfo.fromUser(savedUser, replyStatus.getKey());


//            String accessToken = "Bearer_" + jwtUtils.generateAccessToken(userTokenInfo);
            String accessToken = "Bearer" + jwtUtils.generateAccessToken(userTokenInfo);
            String refreshToken = jwtUtils.generateRefreshToken(userTokenInfo);
            log.info("access token = {}", accessToken);
            saveAuthentication(userTokenInfo);

            return TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("error : ", e);
            throw new AuthException("Login Failed.");
        }
    }

    private void saveAuthentication(UserTokenInfo userTokenInfo) {

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(userTokenInfo.getUserRole().name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userTokenInfo, null, roles);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private GoogleUserInfo getGoogleUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.AUTHORIZATION, "Bearer_" + accessToken);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer" + accessToken);
        final HttpEntity<CustomTokenRequestDTO> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, httpEntity, GoogleUserInfo.class)
                .getBody();
    }
}