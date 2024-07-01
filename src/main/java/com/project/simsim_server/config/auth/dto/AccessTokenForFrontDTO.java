package com.project.simsim_server.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor
public class AccessTokenForFrontDTO {
    private String grantType;
    private String accessToken;
    @Value("${spring.jwt.access.expiration}")
    private Long expiresAt;

    @Builder
    public AccessTokenForFrontDTO(String grantType, String accessToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
    }
}
