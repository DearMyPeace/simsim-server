package com.project.simsim_server.config.auth.dto;

import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtPayloadDTO {
    private Long userId;
    private String userEmail;
    private Role userRole;

    @Builder
    public JwtPayloadDTO(Long userId, String userEmail, Role userRole) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
    }

    public static JwtPayloadDTO fromUser(Users user) {
        return JwtPayloadDTO.builder()
                .userId(user.getUserId())
                .userEmail(user.getEmail())
                .userRole(user.getRole())
                .build();
    }

    public static JwtPayloadDTO fromClaims(Claims claims) {
        return JwtPayloadDTO.builder()
                .userId(claims.get("id", Long.class))
                .userEmail(claims.get("email", String.class))
                .userRole(Role.valueOf(claims.get("role", String.class)))
                .build();
    }
}