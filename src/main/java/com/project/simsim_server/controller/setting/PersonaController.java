package com.project.simsim_server.controller.setting;

import com.project.simsim_server.dto.setting.PersonaResponseDTO;
import com.project.simsim_server.service.setting.PersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/persona")
@CrossOrigin(origins = "*")
@RestController
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public List<PersonaResponseDTO> getPersonaList() {
        return personaService.findAll();
    }
}
