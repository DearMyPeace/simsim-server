package com.project.simsim_server.controller;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.DiaryRequestDTO;
import com.project.simsim_server.dto.DiaryResponseDTO;
import com.project.simsim_server.repository.DiaryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 일기 수정 테스트는 Postman과 DBeaver로 확인
 * (PATCH Request를 보내는 방법 확인 중)
 */
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiaryControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testTemplate;

    @Autowired
    private DiaryRepository diaryRepository;

    @AfterEach
    public void cleanUp() {
        diaryRepository.deleteAll();
    }



    /**
     * 테스트용 다이어리 샘플 등록 함수
     * @return ResponseEntity<DiaryResponseDTO> 일기 저장 후 HTTP 응답 정보
     */
    private ResponseEntity<DiaryResponseDTO> makeTestDiarySample() {
        Long userPK = 8L;
        String content = "DiaryController Test!";
        String url = "http://localhost:" + port + "/api/v1/diary/save";

        DiaryRequestDTO diaryRequestDTO = DiaryRequestDTO.builder()
                .userPk(userPK)
                .content(content)
                .build();

        return testTemplate.postForEntity(url, diaryRequestDTO, DiaryResponseDTO.class);
    }

    @Test
    public void 일기_저장() {
        //given
        ResponseEntity<DiaryResponseDTO> sample = makeTestDiarySample();

        //then
        assertThat(sample.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(sample.getBody())
                .getDiaryPk()).isPositive();

        List<Diary> diaries = diaryRepository.findAll();
        assertThat(diaries.getFirst().getUserPk())
                .isEqualTo(sample.getBody().getUserPk());
        assertThat(diaries.getFirst().getContent())
                .isEqualTo(sample.getBody().getContent());
    }

    @Test
    public void 특정일기_가져오기() {
        //given
        ResponseEntity<DiaryResponseDTO> sample = makeTestDiarySample();

        //when
        Long diaryPk = Objects.requireNonNull(sample.getBody())
                .getDiaryPk();
        String url2 = "http://localhost:" + port + "/api/v1/diary/"
                + diaryPk;
        ResponseEntity<DiaryResponseDTO> getResponseEntity =
                testTemplate.getForEntity(url2, DiaryResponseDTO.class);

        //then
        assertThat(getResponseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(getResponseEntity.getBody())
                .getContent()).isEqualTo(sample.getBody().getContent());

    }

    @Test
    public void 일기_삭제() {
        //given
        ResponseEntity<DiaryResponseDTO> sample = makeTestDiarySample();

        //when
        Long diaryPk = Objects.requireNonNull(sample.getBody()).getDiaryPk();
        String url2 = "http://localhost:" + port + "/api/v1/diary/" + diaryPk;
        testTemplate.delete(url2);

        //then
        assertThat(sample.getStatusCode()).isEqualTo(HttpStatus.OK);

        String url3 = "http://localhost:" + port + "/api/v1/diary/" + diaryPk;
        ResponseEntity<DiaryResponseDTO> getResponseEntity =
                testTemplate.getForEntity(url3, DiaryResponseDTO.class);
        assertThat(Objects.requireNonNull(getResponseEntity.getBody()).getDeleteYn()).isEqualTo("Y");
    }
}