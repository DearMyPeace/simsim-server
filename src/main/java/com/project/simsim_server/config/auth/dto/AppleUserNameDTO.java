package com.project.simsim_server.config.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class AppleUserNameDTO {
    private String firstName;
    private String lastName;
}
