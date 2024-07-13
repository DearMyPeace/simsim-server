package com.project.simsim_server.config.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Component
public class ScheduleConfiguration {

    private final AIBatchService aiBatchService;

    /**
     * 안내 일기 삭제 스케줄
     */
    @Async
    @Scheduled(cron = "0 30 2 * * ?", zone = "Asia/Seoul")
    public void deleteDailyAIReply() {
        log.info("---[SimSimSchedule] 스케줄링 작업 시작---");
        aiBatchService.deleteFirstReply();
        log.info("---[SimSimSchedule] 스케줄링 작업 종료---");
    }
}
