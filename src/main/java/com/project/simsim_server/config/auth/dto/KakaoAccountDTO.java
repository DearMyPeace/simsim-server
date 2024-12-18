package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class KakaoAccountDTO {
    private String email;
    private String name;
    private String birthyear;
    private String birthday;
}
