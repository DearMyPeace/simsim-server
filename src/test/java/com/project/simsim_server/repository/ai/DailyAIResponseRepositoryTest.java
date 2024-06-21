package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class DailyAIResponseRepositoryTest {

    @Autowired
    DailyAiInfoRepository dailyRepository;

    @AfterEach
    public void cleanUp(){
        dailyRepository.deleteAll();
    }


//    @Test
//    public void AI_응답생성() {
//        //given
//        Long userId = 1L;
//
//        //when
//        DailyAiInfo saveData = dailyRepository.save(dailyRepository.save(
//                DailyAiInfo.builder()
//                        .userId(userId)
//                        .targetDate(LocalDate.now())
//                        .build()));
//
//        //then
//        assertThat(saveData.getUserId()).isEqualTo(userId);
//    }

//    @Test
//    public void AI_편지등록() {
//        //given
//        Long userId = 1L;
//
//        DailyAiInfo saveData = dailyRepository.save(dailyRepository.save(
//                DailyAiInfo.builder()
//                        .userId(userId)
//                        .targetDate(LocalDate.now())
//                        .build()));
//        Long aiId = saveData.getAiId();
//
//        String summary = "Hello!";
//        String content = "지수님, 오늘 하루 정말 평화롭고 여유롭게 보내셨군요. "
//                + "맑은 하늘과 따뜻한 햇살, 그리고 신선한 공기 속에서 시작된 하루는 "
//                + "정말 상쾌하게 느껴졌을 것 같아요. 아침 식사로 토스트와 커피를 즐기며 "
//                + "느긋한 시간을 보내신 것도 정말 좋으셨겠어요.\n"
//                + "\n제가 느끼기에 지수님의 하루는 정말 행복과 여유로움이 가득했을 것 같아요. "
//                + "동네 공원에서의 산책과 책 읽기, 친구와의 즐거운 만남과 대화, 그리고 서점에서의 "
//                + "행운까지, 모든 순간이 지수님께 기쁨을 주었을 것 같아요.\n"
//                + "\n마지막으로, 제가 드리고 싶은 작은 솔루션은 이러한 여유롭고 행복한 시간을 더 자주 "
//                + "가지시는 것입니다. 일상의 바쁜 일정 속에서도 잠시 멈춰서 자신만의 시간을 가지는 것이 "
//                + "정말 중요하거든요. 매주나 매달 한 번씩이라도 오늘 같은 여유로운 하루를 계획해 보시면 "
//                + "어떨까요? 앞으로도 지수님께서 행복한 시간을 많이 보내실 수 있기를 바랍니다.";
//
//        //when
//        DailyAiInfo dailyAiInfo = dailyRepository.findById(aiId).get();
//        DailyAiInfo result = dailyAiInfo.updateAiReply(summary, content);
//
//        //then
//        assertThat(result.getReplySummary()).isEqualTo(summary);
//        assertThat(result.getReplyContent()).isEqualTo(content);
//    }
}