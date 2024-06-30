package com.project.simsim_server.config.filter;

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

    private static final String[] AUTHENTICATION_NOT_REQUIRED
            = new String[] { "/", "/login", "/swagger-ui","/v3/api-docs",
            "/api/v1/auth/google", "/reissue"};
    private final JwtUtils jwtUtils;
//    private final String REISSUE_REQUIRED = "/reissue";
//    private final UsersRepository usersRepository;
//    private final RedisService redisService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("---- [SimSimFilter] JwtAuthenticationFilter :시작");
        log.info("---- [SimSimFilter] JwtAuthenticationFilter :요청 URL = {}", request.getRequestURI());

        /**
         * 홈, 로그인 등 첫 페이지는 인증되어 있지 않은 상태이므로 다음 체인으로 넘어가도록 함
         */
        if (StringUtils.startsWithAny(request.getRequestURI(), AUTHENTICATION_NOT_REQUIRED)) {
            log.info("---- [SimSimFilter] JwtAuthenticationFilter :인증이 필요하지 않은 URL입니다.");
            filterChain.doFilter(request, response);
            //return
        }

        Optional<String> accessToken = jwtUtils.extractAccessToken(request);
//        Optional<String> refreshToken = jwtUtils.extractRefreshToken(request);
//        /**
//         * 액세스 토큰 만료 시 리프레시 토큰 유효성 검사 및 액세스 토큰 재발급 - 리프레시 토큰 만료 시 401, 없는 경우 403 에러
//         */
//        if (StringUtils.startsWithAny(request.getRequestURI(), REISSUE_REQUIRED)) {
//            log.info("---- [SimSimFilter] JwtAuthenticationFilter :리프레스 토큰 유효성 검사 시작");
//
//            if (refreshToken.isPresent() && accessToken.isPresent()) {
//                checkRefreshToken(refreshToken);
//                setAuthentication(accessToken.get());
//                filterChain.doFilter(request, response);
//            } else {
//                log.info("---- [SimSimFilter] JwtAuthenticationFilter :리프레시 토큰이 존재하지 않습니다.");
//                SecurityContextHolder.clearContext();
//                throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
//            }
//        }


        /**
         * RefreshToken, AccessToken 유효성 검사 - 만료 시 401, 없으면 403 에러
         */
        if (accessToken.isPresent()) {
            boolean isAccessTokenValid = jwtUtils.validateToken(accessToken.get());
            if (isAccessTokenValid) {
                if (jwtUtils.isTokenExpired(accessToken.get())) {
                    log.info("---- [SimSimFilter] JwtAuthenticationFilter :액세스 토큰의 유효기간이 만료되었습니다.");
                    SecurityContextHolder.clearContext();
                    throw new OAuthException(ACCESS_TOKEN_EXPIRED);
                }
            } else {
                log.info("---- [SimSimFilter] JwtAuthenticationFilter :액세스 토큰이 유효하지 않습니다.");
                SecurityContextHolder.clearContext();
                throw new OAuthException(JWT_NOT_VALID);
            }
        } else {
            log.info("---- [SimSimFilter] JwtAuthenticationFilter :액세스 토큰이 존재하지 않습니다.");
            SecurityContextHolder.clearContext();
            throw new OAuthException(ACCESS_TOKEN_NOT_EXIST);
        }

        /**
         * 액세스 토큰이 유효하면 SecurityContext에 저장
         */
        setAuthentication(accessToken.get());
        filterChain.doFilter(request, response);
    }

//    private void checkRefreshToken(Optional<String> refreshToken) {
//        if (!jwtUtils.validateToken(refreshToken.get())) {
//            log.info("---- [SimSimFilter] JwtAuthenticationFilter :리프레시 토큰이 유효하지 않습니다.");
//            SecurityContextHolder.clearContext();
//            throw new OAuthException(JWT_NOT_VALID);
//        }
//
//        //Redis의 Refresh 토큰과 비교
//        Users users = getUsers(refreshToken.get());
//        Optional<String> optionalRt = redisService.getValues(users.getEmail()).describeConstable();
//        if(optionalRt.isPresent()) {
//            String rt = optionalRt.get();
//            if(!rt.equals(refreshToken.get())) {
//                log.info("---- [SimSimFilter] JwtAuthenticationFilter :리프레시 토큰이 Redis의 리프레시 토큰과 일치하지 않습니다.");
//                SecurityContextHolder.clearContext();
//                throw new OAuthException(JWT_NOT_VALID);
//            }
//        } else {
//            throw new OAuthException(REFRESH_TOKEN_NOT_EXIST);
//        }
//    }

//    private Users getUsers(String accessToken) {
//        String email = jwtUtils.getEmail(accessToken);
//        return usersRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
//    }


    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", request.getRequestURI());
        boolean result =  StringUtils.startsWithAny(request.getRequestURI(), AUTHENTICATION_NOT_REQUIRED);
        log.info("---- [SimSimFilter] JwtAuthenticationFilter : should not filter = {}", result);

        return result;
    }
}