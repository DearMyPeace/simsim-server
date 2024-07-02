package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class AppleUserInfoDTO {
    private AppleUserNameDTO name;
    private String email;
}
