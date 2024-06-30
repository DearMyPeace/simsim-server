package com.project.simsim_server.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenForFrontDTO {
    private String grantType;
    private String accessToken;

    @Builder
    public AccessTokenForFrontDTO(String grantType, String accessToken) {
        this.grantType = grantType;
        this.accessToken = accessToken;
    }
}
