package com.project.simsim_server.config.filter;

import com.project.simsim_server.config.auth.jwt.CustomUserDetails;
import com.project.simsim_server.config.auth.jwt.CustomUserDetailsService;
import com.project.simsim_server.config.auth.jwt.JwtUtils;
import com.project.simsim_server.service.redis.RedisService;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import com.project.simsim_server.exception.OAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.project.simsim_server.exception.AuthErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String[] AUTHENTICATION_NOT_REQUIRED
            = new String[] {"/swagger-ui","/v3/api-docs", "/api/v1/auth/google", "/reissue"};

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
                    CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(userId);
                    setAuthentication(userDetails);
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

    private void setAuthentication(CustomUserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.warn("---- [SimSimFilter] JwtAuthenticationFilter : 생성한 인증 객체 ={}", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", request.getRequestURI());
        boolean result = StringUtils.startsWithAny(request.getRequestURI(), AUTHENTICATION_NOT_REQUIRED);
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", result);
        return result;
    }
}