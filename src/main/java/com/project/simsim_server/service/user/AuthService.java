package com.project.simsim_server.service.user;

import com.project.simsim_server.config.auth.dto.*;
import com.project.simsim_server.config.auth.dto.JwtPayloadDTO;
import com.project.simsim_server.config.auth.jwt.CustomUserDetails;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.exception.OAuthException;
import com.project.simsim_server.exception.UserNotFoundException;
import com.project.simsim_server.service.redis.RedisService;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.user.UsersRepository;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

import static com.project.simsim_server.exception.AuthErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;
    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String BEARER = "Bearer ";
    private final String SERVER = "Server";
    private final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/userinfo/v2/me";
    private final String GOOGLE_CANCLE_URL = "https://accounts.google.com/o/oauth2/revoke?token={access_token}";

    /**
     * Google 소셜 로그인을 통한 회원 가입 및 로그인
     * @param tokenRequest
     * @return
     * @throws AuthException
     */
    @Transactional
    public TokenDTO login(CustomTokenRequestDTO tokenRequest) throws AuthException {

        /**
         * 구글 로그인 Access 토큰으로 회원 정보 가져오기
         */
        try {
            GoogleUserInfoDTO userInfo = getGoogleUserInfo(tokenRequest.getAccess_token());
            String userEmail = userInfo.getEmail();
            String userName = userInfo.getName();

            /**
             * DB 회원 정보 유무 확인
             * - 회원 정보가 있으면 이름만 변경
             * - 회원 정보가 없으면 새로 DB에 등록
             */
            Users user = usersRepository.findByEmail(userEmail)
                    .map((entity) -> entity.update(userName))
                    .orElse(Users.builder()
                            .name(userName)
                            .email(userEmail)
                            .role(Role.USER)
                            .build());
            Users savedUser = usersRepository.save(user);

            /**
             * DB에 저장된 회원정보로 JWT 토큰 발급
             */
            JwtPayloadDTO jwtPayloadDTO
                    = JwtPayloadDTO.fromUser(savedUser);

            String accessToken = BEARER + jwtUtils.generateAccessToken(jwtPayloadDTO);
            String refreshToken = jwtUtils.generateRefreshToken(jwtPayloadDTO);
            log.info("access token = {}", accessToken);
            saveAuthentication(jwtPayloadDTO);

            /**
             * Redis에 Refresh 토큰 저장
             */
            redisService.setValues(jwtPayloadDTO.getUserEmail(), refreshToken,
                    Duration.ofMillis(jwtUtils.getRefreshExpireTime()));

            return TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("에러 : ", e);
            throw new AuthException("로그인 실패");
        }
    }

    private void saveAuthentication(JwtPayloadDTO jwtPayloadDTO) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(jwtPayloadDTO.getUserRole().name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtPayloadDTO, null, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.warn("---- [SimSimFilter] saveAuthentication : 생성한 인증 객체 ={}", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        final HttpEntity<CustomTokenRequestDTO> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, httpEntity, GoogleUserInfoDTO.class)
                .getBody();
    }


    /**
     * 로그 아웃
     * @param accessToken
     * @param userId
     */
    @Transactional
    public void logout(String accessToken, Long userId) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = getPrincipal(resolveAccessToken);

        log.warn("-----[SimsimFilter] AuthService logout principal = {}", principal);

        //DB에 존재하는 회원인지 확인
        Optional<Users> user = usersRepository.findByIdAndUserStatus(Long.parseLong(principal));
        if (user.isEmpty())
            throw new UserNotFoundException("[로그아웃 에러] 해당 유저를 찾을 수 없습니다." + principal,
                    "USER_NOT_FOUND");

        // Redis에서 RefreshToken 삭제
        String userEmail = user.get().getEmail();
        String refreshTokenInRedis = redisService.getValues(userEmail);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues(userEmail);
        }

        log.info(principal + " : " + "logout" + "(" + new Date() + ")");
    }


    /**
     * 회원 탈퇴
     * @param accessToken
     * @param userId
     */
    @Transactional
    public void delete(String accessToken, Long userId) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = getPrincipal(resolveAccessToken);

        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UserNotFoundException("[회원탈퇴 에러] 해당 유저를 찾을 수 없습니다.", "USER_NOT_FOUND");
        Users userInfo = user.get();
        Users deleteUser = userInfo.delete();
        usersRepository.save(deleteUser);

        // Redis에서 RefreshToken 삭제
        String userEmail = user.get().getEmail();
        String refreshTokenInRedis = redisService.getValues(userEmail);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues(userEmail);
        }

        log.info(principal + " : " + "delete" + "(" + new Date() + ")");
    }


    /**
     * 토큰 재발급
     * @param requestRefreshToken
     * @param requestAccessToken
     * @return
     */
    @Transactional
    public TokenDTO reissue(String requestRefreshToken, String requestAccessToken) {

        log.info("----[AuthService] 리프레스 토큰 및 액세스 토큰 재발급 시작 ----");
        String resolveAccessToken = resolveToken(requestAccessToken);
        String principal = getPrincipal(resolveAccessToken);

        Optional<Users> user = usersRepository.findByIdAndUserStatus(Long.parseLong(principal));
        if (user.isEmpty())
            throw new UserNotFoundException("[토큰 재발급 에러] 해당 유저를 찾을 수 없습니다.", "USER_NOT_FOUND");

        if (!jwtUtils.validateToken(requestRefreshToken)) {
            redisService.deleteValues(user.get().getEmail());
            SecurityContextHolder.clearContext();
            return null;
        }

        String refreshTokenInRedis = redisService.getValues(user.get().getEmail());
        if (refreshTokenInRedis == null) {
            SecurityContextHolder.clearContext();
            return null;
        } else if (!requestRefreshToken.equals(refreshTokenInRedis) || jwtUtils.isTokenExpired(requestRefreshToken)) {
            redisService.deleteValues(user.get().getEmail());
            SecurityContextHolder.clearContext();
            return null;
        }

        Authentication authentication = jwtUtils.getAuthentication(requestAccessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = jwtUtils.getAuthorities(authentication);


        JwtPayloadDTO jwtPayloadDTO
                = JwtPayloadDTO.fromUser(user.get());

        String accessToken = BEARER + jwtUtils.generateAccessToken(jwtPayloadDTO);
        String refreshToken = jwtUtils.generateRefreshToken(jwtPayloadDTO);
        redisService.deleteValues(jwtPayloadDTO.getUserEmail());
        saveAuthentication(jwtPayloadDTO);
        log.info("access token = {}", accessToken);

        /**
         * Redis에 Refresh 토큰 저장
         */
        redisService.setValues(jwtPayloadDTO.getUserEmail(), refreshToken,
                Duration.ofMillis(jwtUtils.getRefreshExpireTime()));

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String resolveToken(String accessToken) {
        if (accessToken != null && accessToken.startsWith(BEARER)) {
            return accessToken.substring(7);
        }
        return null;
    }

    /**
     * AccessToken에서 회원 정보 추출
     */
    public String getPrincipal(String requestAccessToken) {
        return jwtUtils.getAuthentication(requestAccessToken).getName();
    }
}
