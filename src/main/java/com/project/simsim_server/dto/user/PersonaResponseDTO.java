package com.project.simsim_server.dto.user;

import com.project.simsim_server.domain.user.Persona;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PersonaResponseDTO {

    private Long personaId;
    private String personaName;
    private String personaCode;

    public PersonaResponseDTO(Persona persona) {
        this.personaId = persona.getPersonaId();
        this.personaName = persona.getPersonaName();
        this.personaCode = persona.getPersonaCode();
    }
}
