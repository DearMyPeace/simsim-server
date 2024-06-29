package com.project.simsim_server.config.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.project.simsim_server.config.auth.dto.JwtPayloadDTO;
import com.project.simsim_server.config.auth.dto.TokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
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
    private CustomUserDetailsService customUserDetailsService;
    private TokenService tokenService;
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";
    private static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "refresh";


    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 안의 회원 정보 반환
     * @param token JWT
     * @return UserTokenInfo JWT 안에 저장된 로그인한 회원 정보
     */
    public JwtPayloadDTO getUserInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload();

        return JwtPayloadDTO.fromClaims(claims);
    }


    /**
     * AccessToken 생성
     * @param jwtPayloadDTO
     * @return AccessToken 반환
     */
    public String generateAccessToken(JwtPayloadDTO jwtPayloadDTO) {
        return generateToken(jwtPayloadDTO, ACCESS_TOKEN_SUBJECT, accessExpireTime);
    }


    /**
     * RefreshToken 생성
     * @param jwtPayloadDTO
     * @return RefreshToken 반환
     */
    public String generateRefreshToken(JwtPayloadDTO jwtPayloadDTO) {
        return generateToken(jwtPayloadDTO, REFRESH_TOKEN_SUBJECT, 10L);
    }


    /**
     * AccessToken 및 RefreshToken 생성
     * @param jwtPayloadDTO
     * @param expireTime
     * @return 토큰 반환
     */
    public String generateToken(JwtPayloadDTO jwtPayloadDTO, String subject, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);
        
        return Jwts.builder()
                .subject(subject)
                .claim("id", jwtPayloadDTO.getUserId())
                .claim("email", jwtPayloadDTO.getUserEmail())
                .claim("role", jwtPayloadDTO.getUserRole().getKey())
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


    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(REFRESH_TOKEN_HEADER));
    }


    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }


    /**
     * 요청으로 전달 된 토큰 검증
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey)
                    .build().parseSignedClaims(token)
                    .getPayload();
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


    public Authentication getAuthentication(String token) {
        JwtPayloadDTO jwtPayloadDTO = getUserInfo(token);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(jwtPayloadDTO.getUserEmail());
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(jwtPayloadDTO.getUserRole().name()));
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }


    private List<GrantedAuthority> getAuthorities(JwtPayloadDTO jwtPayloadDTO) {
        return Collections.singletonList(new SimpleGrantedAuthority(jwtPayloadDTO.getUserRole().name()));
    }

    public String getEmail(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        return jwt.getClaim("email").asString();
    }

    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            TokenDTO token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            if (validateToken(refreshToken)) {
                Authentication authentication = getAuthentication(refreshToken);
                JwtPayloadDTO jwtPayloadDTO = (JwtPayloadDTO) authentication.getPrincipal();
                String reissueAccessToken = generateAccessToken(jwtPayloadDTO);
                tokenService.updateToken(reissueAccessToken, token);
                return reissueAccessToken;
            }
        }
        return null;
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Date expDate = jwt.getExpiresAt();
        Date now = new Date();

        return now.after(expDate);
    }
}
