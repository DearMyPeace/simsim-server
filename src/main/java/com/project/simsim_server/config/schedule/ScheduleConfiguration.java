package com.project.simsim_server.config.schedule;

import com.project.simsim_server.service.ai.DailyAIReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Slf4j
//@RequiredArgsConstructor
//@EnableAsync
//@Component
//public class ScheduleConfiguration {
//
//    private final AIBatchService aiBatchService;
//
//    @Async
//    @Scheduled(cron = "0 48 19 * * ?", zone = "Asia/Seoul")
//    public void generateDailyAIReply() {
//        log.info("---[SimSimSchedul] 스케줄링 작업 시작");
//        aiBatchService.saveAuto();
//        log.info("---[SimSimSchedul] 스케줄링 작업 종료");
//    }
//}
