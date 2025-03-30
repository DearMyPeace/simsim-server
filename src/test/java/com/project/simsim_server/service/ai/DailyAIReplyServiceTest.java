package com.project.simsim_server.service.ai;

import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.AIThumsRequestDTO;
import com.project.simsim_server.exception.ai.AIErrorCode;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.user.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DailyAIReplyServiceTest {

    @Autowired
    private DailyAIReplyService dailyAIReplyService;
    @Autowired
    private UsersRepository usersRepository;
    private final Long TEST_USER_ID = 1L;
    private final Long TEST_AI_ID = 446L;


    @Test
    void 편지피드백_좋아요_확인() {
        // given
        AIThumsRequestDTO sample1 = new AIThumsRequestDTO(TEST_AI_ID, "U");

        // when
        AILetterResponseDTO result1 = dailyAIReplyService.updateThumsStatus(sample1, TEST_USER_ID);

        //then
        assertThat(result1).isNotNull();
        assertThat(result1.getAiId()).isEqualTo(TEST_AI_ID);
        assertThat(result1.getThumbsStatus()).isEqualTo(sample1.getThumsStatus());

    }

    @Test
    void 편지피드백_싫어요_확인() {
        // given
        AIThumsRequestDTO sample2 = new AIThumsRequestDTO(TEST_AI_ID, "D");

        // when
        AILetterResponseDTO result2 = dailyAIReplyService.updateThumsStatus(sample2, TEST_USER_ID);

        //then
        assertThat(result2).isNotNull();
        assertThat(result2.getAiId()).isEqualTo(TEST_AI_ID);
        assertThat(result2.getThumbsStatus()).isEqualTo(sample2.getThumsStatus());
    }


    @Test
    void 없는유저_확인() {
        // given
        AIThumsRequestDTO sample3 = new AIThumsRequestDTO(TEST_AI_ID, "N");

        // when & then
        assertThatThrownBy(() -> dailyAIReplyService.updateThumsStatus(sample3, 10000L))
                .isInstanceOf(AIException.class)
                .extracting(ex -> (AIException) ex)
                .extracting(AIException::getErrorType)
                .isInstanceOf(AIErrorCode.class)
                .satisfies(errorType -> {
                    AIErrorCode aiErrorCode = (AIErrorCode) errorType;
                    assertThat(aiErrorCode.getMessage()).isEqualTo(AIErrorCode.AILETTERS_NOT_FOUND.getMessage());
                    assertThat(aiErrorCode.getCode()).isEqualTo(AIErrorCode.AILETTERS_NOT_FOUND.getCode());
                });
    }

    @Test
    void 없는AIID_확인() {
        // given
        AIThumsRequestDTO sample4 = new AIThumsRequestDTO(100000L, "N");

        // when & then
        assertThatThrownBy(() -> dailyAIReplyService.updateThumsStatus(sample4, TEST_USER_ID))
                .isInstanceOf(AIException.class)
                .extracting(ex -> (AIException) ex)
                .extracting(AIException::getErrorType)
                .isInstanceOf(AIErrorCode.class)
                .satisfies(errorType -> {
                    AIErrorCode aiErrorCode = (AIErrorCode) errorType;
                    assertThat(aiErrorCode.getMessage()).isEqualTo(AIErrorCode.AILETTERS_NOT_FOUND.getMessage());
                    assertThat(aiErrorCode.getCode()).isEqualTo(AIErrorCode.AILETTERS_NOT_FOUND.getCode());
                });
    }

    @Test
    void 유효하지않은값_확인() {
        // given
        AIThumsRequestDTO sample5 = new AIThumsRequestDTO(TEST_AI_ID, "K");

        // when &  then
        assertThatThrownBy(() -> dailyAIReplyService.updateThumsStatus(sample5, TEST_USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 값입니다. : K");
    }

    @Test
    void 페르소나화면선택_확인() {
        // given
        LocalDate date = LocalDate.of(2025, 3, 29);
        AILetterRequestDTO requestDTO = new AILetterRequestDTO(date, "F");
        Users user = usersRepository.findById(TEST_USER_ID).get();

        // when
        dailyAIReplyService.save(requestDTO, user.getUserId());
        assertThat(requestDTO.getPersonaCode()).isEqualTo("F");
        assertThat(requestDTO.getPersonaCode()).isNotEqualTo(user.getUserId());
    }
}