package com.project.simsim_server.controller.setting;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.setting.Persona;
import com.project.simsim_server.dto.setting.PersonaResponseDTO;
import com.project.simsim_server.repository.setting.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonaControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testTemplate;

//    @AfterEach
//    public void cleanUp() {
//        personaRepository.deleteAll();
//    }


    @Test
    public void 페르소나_목록요청() {
        //given
        //DB에 사전에 샘플 저장

        //when
        String url = "http://localhost:" + port + "/api/v1/users/persona";
        ResponseEntity<List<PersonaResponseDTO>> response = testTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PersonaResponseDTO>>() {}
        );

        //then
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody())
                .getLast().getPersonaId()).isPositive();

        List<PersonaResponseDTO> personaList = response.getBody();
        assertThat(personaList.getFirst().getPersonaName()).isEqualTo("사고형");
        assertThat(personaList.get(1).getPersonaCode()).isEqualTo("F");
    }
}