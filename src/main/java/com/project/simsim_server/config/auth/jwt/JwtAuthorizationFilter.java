package com.project.simsim_server.config.auth.jwt;

import com.project.simsim_server.config.auth.dto.JwtPayloadDTO;
import com.project.simsim_server.service.redis.RedisService;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Reply;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import com.project.simsim_server.exception.OAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.simsim_server.exception.AuthErrorCode.*;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Value("${spring.jwt.access.expiration}")
    private Long accessExpireTime;
    @Value("${spring.jwt.refresh.expiration}")
    private Long refreshExpireTime;

    private static final String[] AUTHORIZATION_NOT_REQUIRED = new String[] { "/", "/login", "/swagger-ui","/v3/api-docs", "/api/v1/auth/google"};
    private final JwtUtils jwtUtils;
    private final UsersRepository usersRepository;
    private final DiaryRepository diaryRepository;
    private final RedisService redisService;

    public JwtAuthorizationFilter(JwtUtils jwtUtils, UsersRepository usersRepository, DiaryRepository diaryRepository, RedisService redisService) {

        this.jwtUtils = jwtUtils;
        this.usersRepository = usersRepository;
        this.diaryRepository = diaryRepository;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthorizationProcessingFilter start");
        log.info("request.getRequestURI() = {}", request.getRequestURI());
        if (StringUtils.startsWithAny(request.getRequestURI(),
                AUTHORIZATION_NOT_REQUIRED)) {
            filterChain.doFilter(request, response);
            log.info("AUTHORIZATION_NOT_REQUIRED");
            return;
        }

        //accessToken 확인
        Optional<String> accessToken = jwtUtils.extractAccessToken(request);
        if (accessToken.isPresent()) {
            // 만약 accessToken 존재시 return;
            log.info("accessToken exist");
            boolean isAccessTokenValid = jwtUtils.validateToken(accessToken.get());
            if (isAccessTokenValid) {
                log.info("accessToken valid");
                Authentication authentication = jwtUtils.getAuthentication(accessToken.get());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                if(jwtUtils.isTokenExpired(accessToken.get())) {
                    log.info("access token expired");
                    Optional<String> refreshToken = jwtUtils.extractRefreshToken(request);
                    //refresh token 존재시 accessToken reissue 후 return;
                    if (refreshToken.isPresent()) {
                        //refreshToken valid check
                        checkRefreshToken(response, refreshToken, accessToken);
                    } else {
                        log.info("refresh token not exist");
                        throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
                    }
                } else {
                    log.info("access token not valid");
                    throw new OAuthException(JWT_NOT_VALID);
                }
            }
        } else {
            throw new OAuthException(ACCESS_TOKEN_NOT_EXIST);
        }
        filterChain.doFilter(request, response);
    }

    private void checkRefreshToken(HttpServletResponse response, Optional<String> refreshToken, Optional<String> accessToken) {
        if (!jwtUtils.validateToken(refreshToken.get())) {
            log.info("refresh token not valid");
            throw new OAuthException(JWT_NOT_VALID);
        }
        Users users = getUsers(accessToken.get());
        Optional<String> optionalRt = redisService.getValues(users.getEmail()).describeConstable();
        if(optionalRt.isPresent()) {
            String rt = optionalRt.get();
            if(!rt.equals(refreshToken.get())) {
                throw new OAuthException(JWT_NOT_VALID);
            }
        }
        reIssueToken(users, response);
    }

    private void reIssueToken(Users user, HttpServletResponse response) {
        log.info("checkRefreshTokenAndReIssueAccessToken start");

        Reply replyStatus = Reply.EMPTY;
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Diary> diaries = diaryRepository.findDiariesByCreatedDateAndUserId(yesterday,
                user.getUserId());
        if (!diaries.isEmpty()) {
            replyStatus = Reply.OCCUPIED;
        }
        JwtPayloadDTO jwtPayloadDTO
                = JwtPayloadDTO.fromUser(user);

        String refreshToken = jwtUtils.generateRefreshToken(jwtPayloadDTO);
        String accessToken = jwtUtils.generateAccessToken(jwtPayloadDTO);

        //securityContext에 저장
        saveAuthentication(jwtPayloadDTO);


        //레디스에 리프레시 토큰 저장
        redisService.setValues(user.getEmail(), refreshToken, Duration.ofMillis(refreshExpireTime));

        // AccessToken, RefreshToken 프론트에 전달
        Cookie accessTokenCookie =
                new Cookie("Authorization", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge((int) (accessExpireTime / 1000)); // seconds

        Cookie refreshTokenCookie =
                new Cookie("refresh", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) (refreshExpireTime / 1000)); // seconds

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        log.info("checkRefreshTokenAndReIssueAccessToken end");
    }

    private Users getUsers(String accessToken) {
        String email = jwtUtils.getEmail(accessToken);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private void saveAuthentication(JwtPayloadDTO jwtPayloadDTO) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(jwtPayloadDTO.getUserRole().name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtPayloadDTO, null, roles);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("should not filter = {}", request.getRequestURI());
        boolean result =  StringUtils.startsWithAny(request.getRequestURI(), AUTHORIZATION_NOT_REQUIRED);
        log.info("should not filter = {}", result);

        return result;
    }
}