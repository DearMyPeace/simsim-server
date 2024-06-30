package com.project.simsim_server.config.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenForFrontDTO {
    private String grantType;
    private String accessToken;
    private String principal;

    @Builder
    public AccessTokenForFrontDTO(String grantType, String accessToken, String principal) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.principal = principal;
    }
}
