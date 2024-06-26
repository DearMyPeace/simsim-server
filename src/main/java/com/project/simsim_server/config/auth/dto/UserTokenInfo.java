package com.project.simsim_server.config.auth.dto;

import com.project.simsim_server.domain.user.Grade;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
//public class UserTokenInfo implements UserDetails {
public class UserTokenInfo {

    private Long userId;
    private String userName;
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    @Enumerated(EnumType.STRING)
    private Grade userGrade;

    private int userPieceCnt;
    private String userPersona;
    private String userBgImage;
    private String userStatus;
    private String replyStatus;

    @Builder
    public UserTokenInfo(Long userId, String userName, String userEmail,
                         Role userRole, Grade userGrade, int userPieceCnt,
                         String userPersona, String userBgImage,
                         String userStatus, String replyStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.userGrade = userGrade;
        this.userPieceCnt = userPieceCnt;
        this.userPersona = userPersona;
        this.userBgImage = userBgImage;
        this.userStatus = userStatus;
        this.replyStatus = replyStatus;
    }

    public static UserTokenInfo fromUser(Users user, String replyStatus) {
        return UserTokenInfo.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userRole(user.getRole())
                .userGrade(user.getGrade())
                .userPieceCnt(user.getPieceCnt())
                .userPersona(user.getPersona())
                .userBgImage(user.getBgImage())
                .userStatus(user.getUserStatus())
                .replyStatus(replyStatus)
                .build();
    }

    public static UserTokenInfo fromClaims(Claims claims) {
        return UserTokenInfo.builder()
                .userId(claims.get("id", Long.class))
                .userName(claims.get("name", String.class))
                .userEmail(claims.get("email", String.class))
                .userRole(Role.valueOf(claims.get("role", String.class)))
                .userGrade(Grade.valueOf(claims.get("grade", String.class)))
                .userPieceCnt(claims.get("pieceCnt", Integer.class))
                .userPersona(claims.get("persona", String.class))
                .userBgImage(claims.get("bgImage", String.class))
                .userStatus(claims.get("status", String.class))
                .replyStatus(claims.get("replyStatus", String.class))
                .build();
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.singletonList(new SimpleGrantedAuthority(userRole.getKey()));
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return userEmail;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}
