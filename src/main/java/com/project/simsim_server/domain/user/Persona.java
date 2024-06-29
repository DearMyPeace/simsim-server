package com.project.simsim_server.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "persona_info_tbl")
@Entity
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Long personaId;

    @Column(name = "persona_name", nullable = false)
    private String personaName;

    @Column(name = "persona_code", nullable = false)
    private String personaCode;

    @Builder
    public Persona(Long personaId, String personaName, String personaCode) {
        this.personaId = personaId;
        this.personaName = personaName;
        this.personaCode = personaCode;
    }
}
