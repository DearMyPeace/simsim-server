package com.project.simsim_server.service.ai;

import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.AIThumbsRequestDTO;
import com.project.simsim_server.exception.ai.AIErrorCode;
import com.project.simsim_server.exception.ai.AIException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DailyAIReplyServiceTest {

    @Autowired
    private DailyAIReplyService dailyAIReplyService;
    private final Long TEST_USER_ID = 1L;
    private final Long TEST_AI_ID = 446L;


    @Test
    void 편지피드백_좋아요_확인() {
        // given
        AIThumbsRequestDTO sample1 = new AIThumbsRequestDTO(TEST_AI_ID, "U");

        // when
        AILetterResponseDTO result1 = dailyAIReplyService.updateThumbsStatus(sample1, TEST_USER_ID);

        //then
        assertThat(result1).isNotNull();
        assertThat(result1.getAiId()).isEqualTo(TEST_AI_ID);
        assertThat(result1.getThumbsStatus()).isEqualTo(sample1.getThumbsStatus());

    }

    @Test
    void 편지피드백_싫어요_확인() {
        // given
        AIThumbsRequestDTO sample2 = new AIThumbsRequestDTO(TEST_AI_ID, "D");

        // when
        AILetterResponseDTO result2 = dailyAIReplyService.updateThumbsStatus(sample2, TEST_USER_ID);

        //then
        assertThat(result2).isNotNull();
        assertThat(result2.getAiId()).isEqualTo(TEST_AI_ID);
        assertThat(result2.getThumbsStatus()).isEqualTo(sample2.getThumbsStatus());
    }


    @Test
    void 없는유저_확인() {
        AIThumbsRequestDTO sample3 = new AIThumbsRequestDTO(TEST_AI_ID, "N");

        assertThatThrownBy(() -> dailyAIReplyService.updateThumbsStatus(sample3, 10000L))
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
        AIThumbsRequestDTO sample4 = new AIThumbsRequestDTO(100000L, "N");

        assertThatThrownBy(() -> dailyAIReplyService.updateThumbsStatus(sample4, TEST_USER_ID))
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
        AIThumbsRequestDTO sample5 = new AIThumbsRequestDTO(TEST_AI_ID, "K");
        assertThatThrownBy(() -> dailyAIReplyService.updateThumbsStatus(sample5, TEST_USER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 값입니다. : K");
    }
}