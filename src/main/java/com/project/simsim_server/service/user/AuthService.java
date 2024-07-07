package com.project.simsim_server.service.user;

import com.nimbusds.jose.JWKSet;
import com.project.simsim_server.config.auth.dto.*;
import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.config.auth.jwt.JwtPayload;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.config.redis.TokenDTO;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.user.Provider;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.exception.user.UsersException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.config.redis.RedisService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.auth.AuthErrorCode.*;
import static com.project.simsim_server.exception.user.UsersErrorCode.CANCLE_ACCOUNT;
import static com.project.simsim_server.exception.user.UsersErrorCode.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {

    private final JwtUtils jwtUtils;
    private final AuthenticationService authenticationService;
    private final UsersRepository usersRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final RestTemplate restTemplate;
    private final RedisService redisService;
    private final String BEARER = "Bearer ";
    private final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/userinfo/v2/me";
    private final String GOOGLE_CANCLE_URL = "https://accounts.google.com/o/oauth2/revoke?token={access_token}";
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final String APPLE_CANCLE_URL = "https://appleid.apple.com/auth/revoke";


    /**
     * Google 소셜 로그인을 통한 회원 가입 및 로그인
     */
    @Transactional
    public TokenDTO loginGoogle(GoogleLoginRequestDTO tokenRequest) throws AuthException {
        GoogleUserInfoDTO userInfo = getGoogleUserInfo(tokenRequest.getAccess_token());
        String userEmail = userInfo.getEmail();
        String userName = userInfo.getName();

        Optional<Users> usersOptional = usersRepository.findByEmail(userEmail);
        if (usersOptional.isPresent()) {
            Users existingUser = usersOptional.get();
            if (existingUser.getProviderName() == Provider.APPLE) {
                log.warn("이미 가입한 이메일 주소 입니다.");
                throw new OAuthException(ALREADY_EXIST_ACCOUNT);
            } else if (existingUser.getUserStatus().equals("N")) {
                log.warn("탈퇴한 회원입니다. 다시 아이디를 복원합니다.");
//                    throw new UsersException(CANCLE_ACCOUNT);
            }
            existingUser.update(userName);
            return getTokenDTO(usersRepository.save(existingUser));
        } else {
            Users newUser = Users.builder()
                    .name(userName)
                    .email(userEmail)
                    .role(Role.USER)
                    .providerName(Provider.GOOGLE)
                    .build();
            return getTokenDTO(generateFirstMailForNewUser(newUser));
        }
    }

    private GoogleUserInfoDTO getGoogleUserInfo(String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, BEARER + accessToken);
        final HttpEntity<GoogleLoginRequestDTO> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, httpEntity, GoogleUserInfoDTO.class)
                .getBody();
    }


    /**
     * 애플 소셜 로그인을 통한 회원 가입 및 로그인
     */
    @Transactional
    public TokenDTO loginApple(AppleLoginRequestDTO tokenRequest) throws Exception {
        AppleUserInfoDTO userInfoDTO = getAppleUserInfo(tokenRequest.getAuthorization().getId_token());

        log.warn("애플 페이로드 디코딩 : {}", userInfoDTO);

        final String userEmail;
        final String userName;
        if (tokenRequest.getUser() == null || tokenRequest.getUser().getEmail() == null) {
            userEmail = userInfoDTO.getEmail();
            userName = String.valueOf(userInfoDTO.getName());
        } else {
            userName = tokenRequest.getUser().getName().getFirstName() + tokenRequest.getUser().getName().getLastName();
            userEmail = tokenRequest.getUser().getEmail();
        }

        log.warn("애플 이름 : {}, 이메일: {}", userName, userEmail);

        Optional<Users> usersOptional = usersRepository.findByEmail(userEmail);
        if (usersOptional.isPresent()) {
            Users existingUser = usersOptional.get();
            if (existingUser.getProviderName() == Provider.GOOGLE) {
                log.warn("이미 가입한 이메일 주소 입니다.");
                throw new OAuthException(ALREADY_EXIST_ACCOUNT);
            } else if (existingUser.getUserStatus().equals("N")) {
                log.warn("탈퇴한 회원입니다. 다시 아이디를 복원합니다.");
                throw new UsersException(CANCLE_ACCOUNT);
            }
            return getTokenDTO(existingUser);
        } else {
            Users newUser = Users.builder()
                    .name(userName)
                    .email(userEmail)
                    .role(Role.USER)
                    .providerName(Provider.APPLE)
                    .build();
            return getTokenDTO(generateFirstMailForNewUser(newUser));
        }
    }

    private TokenDTO getTokenDTO(Users savedUser) {
        JwtPayload jwtPayload = JwtPayload.fromUser(savedUser);

        String accessToken = BEARER + jwtUtils.generateAccessToken(jwtPayload);
        String refreshToken = jwtUtils.generateRefreshToken(jwtPayload);
        log.info("로그인 액세스 토큰 = {}", accessToken);
        authenticationService.saveAuthentication(jwtPayload);

        String values = redisService.getValues(jwtPayload.getUserEmail());
        if (!values.isEmpty()) {
            redisService.deleteValues(jwtPayload.getUserEmail());
        }
        redisService.setValues(jwtPayload.getUserEmail(), refreshToken,
                Duration.ofMillis(jwtUtils.getRefreshExpireTime()));

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private Users generateFirstMailForNewUser(Users user) {
        Users savedUser = usersRepository.save(user);
        String content
                = "Dear My Peace 서비스를 이용해주셔서 감사합니다.🥰\n\n"
                + "기록을 보내시면 편지✉️가 도착합니다.\n"
                + "화면 오른쪽 상단의 설정창에서 📝편지작성자를 골라서 편지를 받을 수 있습니다.\n"
                + "지금은 감정형 (F)로 설정되어있습니다.\n"
                + "기록은 하루에 한 번만 보낼 수 있으니 유의해주세요! ";

        DailyAiInfo sampleLetter = DailyAiInfo.builder()
                .userId(savedUser.getUserId())
                .targetDate(toLocalDate(savedUser.getCreatedDate(), ZoneId.of("Asia/Seoul")))
                .diarySummary("일기 작성 및 AI 편지 수령 방법 안내")
                .replyContent(content)
                .replyStatus("N")
                .isFirst(true)
                .build();
        dailyAiInfoRepository.save(sampleLetter);
        return savedUser;
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
     */
    @Transactional
    public void logout(String accessToken) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = authenticationService.getPrincipal(resolveAccessToken);

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
     */
    @Transactional
    public void delete(String accessToken, Long userId) {
        String resolveAccessToken = resolveToken(accessToken);
        String principal = authenticationService.getPrincipal(resolveAccessToken);

        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UsersException(USER_NOT_FOUND);
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
        String principal = authenticationService.getPrincipal(requestRefreshToken);

        log.warn("----[AuthService] 프린시펄 : {}", principal);

        Optional<Users> user = usersRepository.findByEmailAndUserStatus(principal);
        if (user.isEmpty())
            throw new UsersException(USER_NOT_FOUND);

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


        JwtPayload jwtPayload
                = JwtPayload.fromUser(user.get());

        String accessToken = BEARER + jwtUtils.generateAccessToken(jwtPayload);
        String refreshToken = jwtUtils.generateRefreshToken(jwtPayload);
        redisService.deleteValues(jwtPayload.getUserEmail());
        authenticationService.saveAuthentication(jwtPayload);
        log.info("---- [SimSimFilter] reissue 액세스 토큰 = {}", accessToken);

        /**
         * Redis에 Refresh 토큰 저장
         */
        redisService.setValues(jwtPayload.getUserEmail(), refreshToken,
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


    private LocalDate toLocalDate(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toLocalDate();
    }
}
