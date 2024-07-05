package com.project.simsim_server.controller.user;

import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.service.user.PersonaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Persona", description = "[관리자] 페르소나 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/persona")
@RestController
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public List<PersonaResponseDTO> getPersonaList() {
        return personaService.findAll();
    }
}
