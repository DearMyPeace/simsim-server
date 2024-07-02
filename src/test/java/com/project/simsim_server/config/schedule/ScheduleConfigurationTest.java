//package com.project.simsim_server.config.schedule;
//
//
//import com.project.simsim_server.service.ai.DailyAIReplyService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//@ExtendWith({SpringExtension.class, MockitoExtension.class})
//public class ScheduleConfigurationTest {
//
//    @MockBean
//    private DailyAIReplyService dailyAIReplyService;
//
//    @Autowired
//    private ScheduleConfiguration scheduleConfiguration;
//
//    @Test
//    public void testGenerateDailyAIReply() {
//        // 스케줄링 메서드를 직접 호출하여 테스트
//        scheduleConfiguration.generateDailyAIReply();
//
//        // DailyAIReplyService의 save 메서드가 호출되었는지 검증
//        verify(dailyAIReplyService, times(1)).save();
//    }
//}