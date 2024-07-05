package com.project.simsim_server.dto.user;

import com.project.simsim_server.domain.user.Grade;
import com.project.simsim_server.domain.user.Provider;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponseDTO {
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private Grade grade;
    private int pieceCnt;
    private Provider providerName;
    private String personaCode;
    private String personaName;
    private String bgImage;
    private String userStatus;
    private String replyStatus;

    public UserInfoResponseDTO(Users userEntity, String replyStatus, String personaName) {
        this.userId = userEntity.getUserId();
        this.name = userEntity.getName();
        this.email = userEntity.getEmail();
        this.role = userEntity.getRole();
        this.grade = userEntity.getGrade();
        this.pieceCnt = userEntity.getPieceCnt();
        this.providerName = userEntity.getProviderName();
        this.personaCode = userEntity.getPersona();
        this.personaName = personaName;
        this.bgImage = userEntity.getBgImage();
        this.userStatus = userEntity.getUserStatus();
        this.replyStatus = replyStatus;
    }
}
