package com.project.simsim_server.config.auth.dto;

import lombok.Getter;

@Getter
public class CustomTokenRequestDTO {
    private String access_token;
    private String authuser;
    private String prompt;
    private String scope;
    private String token_type;
    private int expires_in;
}
