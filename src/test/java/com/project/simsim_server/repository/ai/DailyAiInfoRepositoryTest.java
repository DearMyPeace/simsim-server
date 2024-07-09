package com.project.simsim_server.repository.ai;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DailyAiInfoRepositoryTest {

    @Autowired
    private DailyAiInfoRepository dailyAiInfoRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Test
    void 편지및요약내용_암호화() {
        // given
        String summary = "답장 요약 암호화 테스트";
        String reply = "답장 내용 암호화";

        DailyAiInfo info
                = DailyAiInfo.builder()
                .userId(1L)
                .targetDate(LocalDate.now())
                .diarySummary(summary)
                .replyContent(reply)
                .happyCnt(0)
                .appreciationCnt(0)
                .loveCnt(0)
                .analyzePositiveTotal(0)
                .tranquilityCnt(0)
                .curiosityCnt(0)
                .surpriseCnt(0)
                .analyzeNeutralTotal(0)
                .sadCnt(0)
                .angryCnt(0)
                .fearCnt(0)
                .analyzeNegativeTotal(0)
                .replyStatus("N")
                .isFirst(false)
                .build();

        // when
        DailyAiInfo save = dailyAiInfoRepository.save(info);

        // then
        assertThat(save.getDiarySummary()).isEqualTo(summary);
        assertThat(save.getReplyContent()).isEqualTo(reply);
        System.out.println("save.getDiarySummary() = " + save.getDiarySummary());
        System.out.println("save.getReplyContent() = " + save.getReplyContent());
    }

    @Test
    void 기존저장편지및요약_암호화() {
        // given
        Optional<DailyAiInfo> data = dailyAiInfoRepository.findById(1L);

        // when
        String summary = null;
        String reply = null;
        if (data.isPresent()) {
            DailyAiInfo info = data.get();
            summary = info.getDiarySummary();
            reply = info.getReplyContent();
            System.out.println("save.getDiarySummary() = " + summary);
            System.out.println("save.getReplyContent() = " + reply);
            info.updateAiResult(info.getDiarySummary(), info.getReplyContent());
            dailyAiInfoRepository.save(info);
        }

        // then
        DailyAiInfo result = dailyAiInfoRepository.findById(1L).get();
        assertThat(result.getDiarySummary()).isEqualTo(summary);
        assertThat(result.getReplyContent()).isEqualTo(reply);
    }

    @Test
    void 암호화문_프론트전달() {
        // given
        Optional<DailyAiInfo> data = dailyAiInfoRepository.findById(3L);
        String originSummary = "첫 일기 작성, 배포 직전 긴장, 식욕 저하, 저장/삭제/수정 기능 정상 작동 확인.";
        String originContent = "첫 번째 일기 작성 축하드립니다! 배포 직전이라 긴장되고 떨리는 건 당연한 감정이에요. 중요한 순간을 앞두고 있다는 건 그만큼 준비를 잘 해왔다는 증거니까요. 요즘 배가 별로 안 고프다는 부분은 스트레스나 긴장 때문일 수 있어요. 중요한 일 앞두고는 식욕이 줄어들기도 하니까요. 저장, 삭제, 수정이 잘 되는 걸 보니 프로젝트가 잘 진행되고 있는 것 같아 다행입니다. 나머지도 잘 될 거예요. 지금의 기분 좋은 감정을 잘 유지하면서 앞으로도 차근차근 나아가세요. 응원합니다!";
        AILetterResponseDTO responseDTO = null;

        // when
        if (data.isPresent()) {
            responseDTO = new AILetterResponseDTO(data.get());
        }

        // then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getSummary()).isEqualTo(originSummary);
        assertThat(responseDTO.getContent()).isEqualTo(originContent);
        System.out.println("responseDTO.getContent() = " + responseDTO.getContent());
    }
}