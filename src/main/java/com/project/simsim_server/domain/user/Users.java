package com.project.simsim_server.domain.user;

import com.project.simsim_server.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor
@Table(name = "users_tbl")
@Entity
public class Users extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private Role role;

    @Column(name = "user_grade", nullable = false)
    @ColumnDefault("0")
    private Grade grade;

    @Column(name = "user_piece_cnt", nullable = false)
    @ColumnDefault("0")
    private int pieceCnt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_provider", nullable = false)
    private Provider providerName;

    @Column(name = "user_persona", nullable = false)
    @ColumnDefault("'F'")
    private String persona;

    @Column(name = "user_bg_image")
    private String bgImage;

    @Column(name = "user_status", nullable = false)
    @ColumnDefault("'Y'")
    private String userStatus;

    @Builder
    public Users(String name, String email, Role role, Provider providerName) {
        byte[] bytes = name.getBytes(StandardCharsets.ISO_8859_1);
        this.name = new String(bytes, StandardCharsets.UTF_8);
        this.email = email;
        this.role = role;
        this.grade = Grade.GENERAL;
        this.pieceCnt = 0;
        this.providerName = providerName;
        this.persona = "F";
        this.userStatus = "Y";
    }

    public Users update(String name) {
        this.name = name;
        this.userStatus = "Y";
        return this;
    }

    public String updateGrade(Grade grade) {
        this.grade = grade;
        return this.grade.getKey();
    }

    public String updatePersona(String persona) {
        this.persona = persona;
        return this.persona;
    }

    public String updateBgImg(String bgimg) {
        this.bgImage = bgimg;
        return this.bgImage;
    }

    public Users delete() {
        this.userStatus = "N";
        return this;
    }

    public Users updatePuzzle(int cnt) {
        this.pieceCnt = cnt;
        return this;
    }

    public Role updateRole(Role role) {
        this.role = role;
        return this.role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

}

