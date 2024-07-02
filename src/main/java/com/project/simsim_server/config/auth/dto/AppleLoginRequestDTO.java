package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class AppleLoginRequestDTO {
    private AppleAuthorizationDTO authorization;
    private AppleUserInfoDTO user;
}
