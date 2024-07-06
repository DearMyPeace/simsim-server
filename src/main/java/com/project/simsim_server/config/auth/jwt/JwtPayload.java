package com.project.simsim_server.config.auth.jwt;

import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtPayload {
    private Long userId;
    private String userEmail;
    private Role userRole;

    @Builder
    public JwtPayload(Long userId, String userEmail, Role userRole) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    public static JwtPayload fromUser(Users user) {
        return JwtPayload.builder()
                .userId(user.getUserId())
                .userEmail(user.getEmail())
                .userRole(user.getRole())
                .build();
    }

    public static JwtPayload fromClaims(Claims claims) {
        return JwtPayload.builder()
                .userId(claims.get("id", Long.class))
                .userEmail(claims.get("email", String.class))
                .userRole(Role.valueOf(claims.get("role", String.class)))
                .build();
    }
}