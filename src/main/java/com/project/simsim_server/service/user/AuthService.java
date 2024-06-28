package com.project.simsim_server.service.user;

import com.project.simsim_server.config.auth.dto.*;
import com.project.simsim_server.config.auth.dto.JwtPayloadDTO;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.domain.user.Role;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

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
             * JWT 토큰 발급
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
            redisService.setValues(savedUser.getEmail(), refreshToken,
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
    }

    private GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        final HttpEntity<CustomTokenRequestDTO> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, httpEntity, GoogleUserInfoDTO.class)
                .getBody();
    }


    @Transactional
    public void logout(String accessToken, Long userId) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = getPrincipal(resolveAccessToken);

        //DB에 존재하는 회원인지 확인
        Optional<Users> user = usersRepository.findByUserStatusAndEmail(principal);
        if (user.isEmpty())
            throw new UserNotFoundException("[로그아웃 에러] 해당 유저를 찾을 수 없습니다. 검색한 Email : " + principal,
                    "USER_NOT_FOUND");

        // Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues(principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues(principal);
        }

        // Redis에 로그아웃 처리한 AT 저장
        long expiration = jwtUtils.getAccessExpireTime() - new Date().getTime();
        redisService.setValues("logout " + principal, accessToken, Duration.ofMillis(expiration));
        log.info(principal + " : " + "logout" + "(" + new Date() + ")");
    }



    @Transactional
    public void delete(String accessToken, Long userId) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = getPrincipal(resolveAccessToken);

        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty())
            throw new UserNotFoundException("[회원탈퇴 에러] 해당 유저를 찾을 수 없습니다.", "USER_NOT_FOUND");
        Users userInfo = user.get();
        Users deleteUser = userInfo.delete();
        usersRepository.save(deleteUser);

        // Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues(principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues(principal);
        }
        // Redis에 회원탈퇴 처리한 AT 저장
        long expiration = jwtUtils.getAccessExpireTime() - new Date().getTime();
        redisService.setValues("delete " + principal, accessToken, Duration.ofMillis(expiration));

        log.info(principal + " : " + "delete" + "(" + new Date() + ")");
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
