package com.project.simsim_server.service.user;

import com.nimbusds.jose.JWKSet;
import com.project.simsim_server.config.auth.dto.*;
import com.project.simsim_server.config.auth.dto.JwtPayloadDTO;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import com.project.simsim_server.domain.user.Provider;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.exception.auth.OAuthException;
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

import java.net.URL;
import java.security.interfaces.RSAKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.auth.AuthErrorCode.*;

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
    private final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/userinfo/v2/me";
    private final String GOOGLE_CANCLE_URL = "https://accounts.google.com/o/oauth2/revoke?token={access_token}";
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final String APPLE_CANCLE_URL = "https://appleid.apple.com/auth/revoke";


    /**
     * Google 소셜 로그인을 통한 회원 가입 및 로그인
     * @param tokenRequest
     * @return
     * @throws AuthException
     */
    @Transactional
    public TokenDTO loginGoogle(GoogleLoginRequestDTO tokenRequest) throws AuthException {

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
            Optional<Users> usersOptional = usersRepository.findByEmail(userEmail);
            if (usersOptional.isPresent() && usersOptional.get().getUserStatus().equals("N")) {
                log.warn("탈퇴한 회원입니다. 다시 아이디를 복원합니다.");
//                throw new UserNotFoundException("탈퇴한 회원입니다.", "USER_NOT_FOUND");
            }

            if (usersOptional.get().getProviderName() == Provider.APPLE) {
                log.warn("이미 가입한 이메일 주소 입니다.");
                throw new OAuthException(ALREADY_EXIST_ACCOUNT);
            }

            Users user = usersOptional.map((entity) -> entity.update(userName))
                    .orElse(Users.builder()
                            .name(userName)
                            .email(userEmail)
                            .role(Role.USER)
                            .providerName(Provider.GOOGLE)
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
            String values = redisService.getValues(jwtPayloadDTO.getUserEmail());
            if (!values.isEmpty()) {
                redisService.deleteValues(jwtPayloadDTO.getUserEmail());
            }
            redisService.setValues(jwtPayloadDTO.getUserEmail(), refreshToken,
                    Duration.ofMillis(jwtUtils.getRefreshExpireTime()));

            return TokenDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthException(LOGIN_FAILED);
        }
    }

    private GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        final HttpEntity<GoogleLoginRequestDTO> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, httpEntity, GoogleUserInfoDTO.class)
                .getBody();
    }

    @Transactional
    public TokenDTO loginApple(AppleLoginRequestDTO tokenRequest) throws Exception {
        /**
         * 애플 유효성 검사
         */
        AppleUserInfoDTO userInfoDTO
                = getAppleUserInfo(tokenRequest.getAuthorization().getId_token());

        log.warn("애플 페이로드 디코딩 : {}", userInfoDTO);

        String userEmail = null;
        String userName = null;
        if (tokenRequest.getUser() == null || tokenRequest.getUser().getEmail() == null) {
            userEmail = userInfoDTO.getEmail();
            userName = String.valueOf(userInfoDTO.getName());
        } else {
            userName = tokenRequest.getUser().getName().getFirstName()
                + tokenRequest.getUser().getName().getLastName();
            userEmail = tokenRequest.getUser().getEmail();
        }

        log.warn("애플 이름 : {}, 이메일: {}", userName, userEmail);

        /**
         * DB 회원 정보 유무 확인
         * - 회원 정보가 있으면 이름만 변경
         * - 회원 정보가 없으면 새로 DB에 등록
         */
        Optional<Users> usersOptional 
                = usersRepository.findByEmail(userEmail);

        if (usersOptional.get().getProviderName() == Provider.GOOGLE) {
            log.warn("이미 가입한 이메일 주소 입니다.");
            throw new OAuthException(ALREADY_EXIST_ACCOUNT);
        }
        if (usersOptional.isPresent() && usersOptional.get().getUserStatus().equals("N")) {
            log.warn("탈퇴한 회원입니다.");
            throw new UserNotFoundException("탈퇴한 회원입니다.", "USER_NOT_FOUND");
        }

        Users user = usersOptional.orElse(Users.builder()
                        .name(userName)
                        .email(userEmail)
                        .role(Role.USER)
                        .providerName(Provider.APPLE)
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
        String values = redisService.getValues(jwtPayloadDTO.getUserEmail());
        if (!values.isEmpty()) {
            redisService.deleteValues(jwtPayloadDTO.getUserEmail());
        }
        redisService.setValues(jwtPayloadDTO.getUserEmail(), refreshToken,
                Duration.ofMillis(jwtUtils.getRefreshExpireTime()));

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private List<RSAKey> getApplePublicKeys() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String jwks = restTemplate.getForObject(new URL(APPLE_KEYS_URL).toURI(), String.class);
        JWKSet jwkSet = JWKSet.parse(jwks);
        return jwkSet.getKeys().stream()
                .map(k -> (RSAKey) k)
                .collect(Collectors.toList());
    }

    private AppleUserInfoDTO getAppleUserInfo(String idToken) throws Exception {
        return jwtUtils.decodePayload(idToken, AppleUserInfoDTO.class);
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

        // Redis에서 RefreshToken 삭제
        String refreshTokenInRedis = redisService.getValues(principal);
        if (refreshTokenInRedis != null) {
            redisService.deleteValues(principal);
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
     * @return
     */
    @Transactional
    public TokenDTO reissue(String requestRefreshToken) {

        log.info("----[AuthService] 리프레스 토큰 및 액세스 토큰 재발급 시작 ----");
        String principal = getPrincipal(requestRefreshToken);

        log.warn("----[AuthService] 프린시펄 : {}", principal);

        Optional<Users> user = usersRepository.findByEmailAndUserStatus(principal);
        if (user.isEmpty())
            throw new UserNotFoundException("[토큰 재발급 에러] 해당 유저를 찾을 수 없습니다.", "USER_NOT_FOUND");

        log.warn("----[AuthService] 유저 정보 : {}", user.get().getEmail());


        if (!jwtUtils.validateToken(requestRefreshToken)) {
            redisService.deleteValues(user.get().getEmail());
            SecurityContextHolder.clearContext();
        }

        String refreshTokenInRedis = redisService.getValues(principal);
        if (refreshTokenInRedis == null) {
            SecurityContextHolder.clearContext();
        } else if (!requestRefreshToken.equals(refreshTokenInRedis) || jwtUtils.isTokenExpired(requestRefreshToken)) {
            redisService.deleteValues(principal);
            SecurityContextHolder.clearContext();
        }


        JwtPayloadDTO jwtPayloadDTO
                = JwtPayloadDTO.fromUser(user.get());

        String accessToken = BEARER + jwtUtils.generateAccessToken(jwtPayloadDTO);
        String refreshToken = jwtUtils.generateRefreshToken(jwtPayloadDTO);
        redisService.deleteValues(jwtPayloadDTO.getUserEmail());
        saveAuthentication(jwtPayloadDTO);
        log.info("---- [SimSimFilter] reissue 액세스 토큰 = {}", accessToken);

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

    private void saveAuthentication(JwtPayloadDTO jwtPayloadDTO) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(jwtPayloadDTO.getUserRole().name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtPayloadDTO, null, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.warn("---- [SimSimFilter] saveAuthentication : 생성한 인증 객체 ={}", SecurityContextHolder.getContext().getAuthentication().getName());
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