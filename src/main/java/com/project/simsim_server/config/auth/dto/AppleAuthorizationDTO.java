package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AppleAuthorizationDTO {
    private String code;
    private String id_token;
    private String state;
}
