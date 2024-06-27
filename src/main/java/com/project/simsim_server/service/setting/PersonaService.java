package com.project.simsim_server.service.setting;

import com.project.simsim_server.domain.setting.Persona;
import com.project.simsim_server.dto.diary.DiaryResponseDTO;
import com.project.simsim_server.dto.setting.PersonaResponseDTO;
import com.project.simsim_server.repository.setting.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    public List<PersonaResponseDTO> findAll() {
        List<Persona> personaList = personaRepository.findAll();
        if (personaList.isEmpty())
            throw new RuntimeException("데이터베이스에 페르소나 정보가 등록되어 있지 않습니다.");
        return personaList.stream()
                .map(PersonaResponseDTO::new)
                .toList();
    }
}
