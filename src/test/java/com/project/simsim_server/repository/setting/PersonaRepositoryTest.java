package com.project.simsim_server.repository.setting;

import com.project.simsim_server.domain.setting.Persona;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class PersonaRepositoryTest {

    @Autowired
    PersonaRepository personaRepository;

    @AfterEach
//    public void cleanUp(){
//        personaRepository.deleteAll();
//    }

    @Test
    public void 모든_페르소나정보_불러오기() {
        //given
        //DB에 정보 저장

        //when
        List<Persona> personaList = personaRepository.findAll();

        //then
        assertThat(personaList.size()).isEqualTo(2);
        assertThat(personaList.getFirst().getPersonaCode()).isEqualTo("T");
    }
}