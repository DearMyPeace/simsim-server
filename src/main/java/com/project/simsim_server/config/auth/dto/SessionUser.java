package com.project.simsim_server.config.auth.dto;

import com.project.simsim_server.domain.user.Users;
import lombok.Getter;

@Getter
public class SessionUser {
    private String name;
    private String email;

    public SessionUser(Users user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }

}
