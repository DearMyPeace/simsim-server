package com.project.simsim_server.domain.user;

import com.project.simsim_server.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "user_persona", nullable = false)
    @ColumnDefault("'P'")
    private String persona;

    @Column(name = "user_bg_image")
    private String bgImage;

    @Column(name = "user_status", nullable = false)
    @ColumnDefault("'Y'")
    private String userStatus;

    @Builder
    public Users(String name, String email, Role role) {
        byte[] bytes = name.getBytes(StandardCharsets.ISO_8859_1);
        this.name = new String(bytes, StandardCharsets.UTF_8);
        this.email = email;
        this.role = role;
        this.grade = Grade.GENERAL;
        this.pieceCnt = 0;
        this.persona = "P";
        this.userStatus = "Y";
    }

    public Users update(String name) {
        this.name = name;
        return this;
    }

    public Users updateGrade(Grade grade) {
        this.grade = grade;
        return this;
    }

    public Users delete() {
        this.userStatus = "N";
        return this;
    }

    public Users updatePuzzle(int cnt) {
        this.pieceCnt = cnt;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}

