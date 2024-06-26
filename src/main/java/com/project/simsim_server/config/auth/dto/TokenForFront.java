package com.project.simsim_server.config.auth.dto;

import lombok.Builder;

@Builder
public class TokenForFront {
    private String grantType;
    private String accessToken;
}
