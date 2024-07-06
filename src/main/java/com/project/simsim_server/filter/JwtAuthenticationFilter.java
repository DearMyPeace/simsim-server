package com.project.simsim_server.filter;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.config.auth.jwt.CustomUserDetails;
import com.project.simsim_server.config.auth.jwt.CustomUserDetailsService;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.config.redis.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.project.simsim_server.exception.auth.AuthErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;

    private static final String[] AUTHENTICATION_NOT_REQUIRED
            = new String[] {"/swagger-ui","/v3/api-docs", "/api/v1/auth/apple", "/api/v1/auth/google", "/api/v1/auth/reissue"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : 시작");
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : 요청 URL = {}", request.getRequestURI());

        if (StringUtils.startsWithAny(request.getRequestURI(), AUTHENTICATION_NOT_REQUIRED)) {
            log.info("---- [SimSimFilter] JwtAuthenticationFilter : 인증이 필요하지 않은 URL입니다.");
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> accessToken = jwtUtils.extractAccessToken(request);

        if (accessToken.isPresent()) {
            boolean isAccessTokenValid = jwtUtils.validateToken(accessToken.get());
            if (isAccessTokenValid) {
                if (jwtUtils.isTokenExpired(accessToken.get())) {
                    log.info("---- [SimSimFilter] JwtAuthenticationFilter : 액세스 토큰의 유효기간이 만료되었습니다.");
                    SecurityContextHolder.clearContext();
                    throw new OAuthException(ACCESS_TOKEN_EXPIRED);
                } else {
                    String userId = jwtUtils.getUserId(accessToken.get());
                    String userEmail = jwtUtils.getEmail(accessToken.get());
                    String values = redisService.getValues(userEmail);

                    log.warn("---- [SimSimFilter] JwtAuthenticationFilter : 검증 이메일 ={}", userEmail);
                    log.warn("---- [SimSimFilter] JwtAuthenticationFilter : 레디스 토큰 ={}", values);

                    if (values.isEmpty()) {
                        log.error("---- [SimSimFilter] JwtAuthenticationFilter : 리프레시 토큰이 없습니다.");
                        throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
                    }

                    CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);
                    authenticationService.setAuthentication(userDetails);
                }
            } else {
                log.info("---- [SimSimFilter] JwtAuthenticationFilter : 액세스 토큰이 유효하지 않습니다.");
                SecurityContextHolder.clearContext();
                throw new OAuthException(JWT_NOT_VALID);
            }
        } else {
            log.info("---- [SimSimFilter] JwtAuthenticationFilter : 액세스 토큰이 존재하지 않습니다.");
            SecurityContextHolder.clearContext();
            throw new OAuthException(ACCESS_TOKEN_NOT_EXIST);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", request.getRequestURI());
        boolean result = StringUtils.startsWithAny(request.getRequestURI(), AUTHENTICATION_NOT_REQUIRED);
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", result);
        return result;
    }
}
