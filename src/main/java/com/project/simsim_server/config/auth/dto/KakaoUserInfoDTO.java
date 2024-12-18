package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class KakaoUserInfoDTO {
    private String id;
    private KakaoAccountDTO accountInfo;
}
