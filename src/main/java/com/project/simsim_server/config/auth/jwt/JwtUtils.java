package com.project.simsim_server.config.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.config.redis.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.auth.AuthErrorCode.INVALID_JWT_SIGNATURE;
import static com.project.simsim_server.exception.auth.AuthErrorCode.JWT_NOT_VALID;

@ToString
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    @Getter
    @Value("${spring.jwt.access.expiration}")
    private Long accessExpireTime;
    @Getter
    @Value("${spring.jwt.refresh.expiration}")
    private Long refreshExpireTime;
    @Value("${spring.jwt.key1}")
    private String key;

    private SecretKey secretKey;
    private static final String BEARER = "Bearer ";
    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "refresh";
    private final DiaryRepository diaryRepository;
    private final RedisService redisService;

    /**
     * Secretkey 생성
     */
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * JWT 안의 회원 정보 반환
     * @param token JWT
     * @return UserTokenInfo JWT 안에 저장된 로그인한 회원 정보
     */
    public JwtPayload getUserInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();

        return JwtPayload.fromClaims(claims);
    }


    /**
     * AccessToken 생성
     * @param jwtPayload
     * @return AccessToken 반환
     */
    public String generateAccessToken(JwtPayload jwtPayload) {
        return generateToken(jwtPayload, accessExpireTime);
    }


    /**
     * RefreshToken 생성
     * @param jwtPayload
     * @return RefreshToken 반환
     */
    public String generateRefreshToken(JwtPayload jwtPayload) {
        return generateToken(jwtPayload, refreshExpireTime);
    }


    /**
     * AccessToken 및 RefreshToken 생성
     * @param jwtPayload
     * @param expireTime
     * @return 토큰 반환
     */
    public String generateToken(JwtPayload jwtPayload, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        log.warn("토큰 유효시간 확인 : {}", expireDate);

        return Jwts.builder()
                .subject(jwtPayload.getUserEmail())
                .claim("id", jwtPayload.getUserId())
                .claim("email", jwtPayload.getUserEmail())
                .claim("role", jwtPayload.getUserRole().getKey())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }


    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(ACCESS_TOKEN_HEADER))
                .filter(at -> at.startsWith(BEARER))
                .map(at -> at.replace(BEARER, ""));
    }



    /**
     * 요청으로 전달 된 토큰 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            log.info("[JwtUtils validateToken()] Claim Payload: {}", claims);
            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired or invalid JWT token", e);
        } catch (SignatureException | MalformedJwtException e) {
            throw new JwtException("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT claims string is empty", e);
        }
    }

    public String getEmail(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.get("email", String.class);

    }

    public String getUserId(String accessToken) {
        Claims claims = parseClaims(accessToken);
        log.warn("디코딩 토큰: {}", claims.get("id", Long.class));
        return claims.get("id", Long.class).toString() ;
    }




    public boolean isTokenExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Date expDate = jwt.getExpiresAt();
        Date now = new Date();

        return now.after(expDate);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (MalformedJwtException e) {
            throw new OAuthException(JWT_NOT_VALID);
        } catch (SecurityException e) {
            throw new OAuthException(INVALID_JWT_SIGNATURE);
        }
    }

    public <T> T decodePayload(String token, Class<T> targetClass) {

        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}
