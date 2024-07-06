package com.project.simsim_server.config.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final JwtUtils jwtUtils;

    // 인증 객체 정보 저장 (유저 식별 번호)
    public void setAuthentication(CustomUserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.warn("---- [SimSimFilter] JwtAuthenticationFilter : 생성한 인증 객체 ={}", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    // 페이로드에서 인증 객체 가져오기
    public void saveAuthentication(JwtPayload jwtPayload) {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(jwtPayload.getUserRole().name()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(jwtPayload, null, roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.warn("---- [SimSimFilter] saveAuthentication : 생성한 인증 객체 ={}", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    // 인증 객체에서 정보 가져오기 (유저 식별 번호)
    public Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName();
        return Long.parseLong(userIdString);
    }

    // 인증 객체 정보 가져오기 (이메일)
    public String getPrincipal(String requestAccessToken) {
        return getAuthentication(requestAccessToken).getName();
    }

    // 페이로드에서 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtils.parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        log.info("---[SimSimInfo] 사용자 권한 ={}", authorities.get(0));

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 사용자 권한 확인
    public List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(new SimpleGrantedAuthority(
                claims.get("role").toString()));
    }
}
