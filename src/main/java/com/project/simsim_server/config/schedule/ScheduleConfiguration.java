package com.project.simsim_server.config.schedule;

import com.project.simsim_server.config.schedule.migration.DiaryMigrationRunner;
import com.project.simsim_server.config.schedule.migration.ReplyMigrationRunner;
import com.project.simsim_server.config.schedule.migration.UsersMigrationRunner;
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

    @Async
    @Scheduled(cron = "0 30 2 * * ?", zone = "Asia/Seoul")
    public void deleteDailyAIReply() {
        log.info("---[SimSimSchedule] 스케줄링 작업 시작---");
        aiBatchService.deleteFirstReply();
        log.info("---[SimSimSchedule] 스케줄링 작업 종료---");
    }

    @Async
    @Scheduled(cron = "0 45 17 * * ?", zone = "Asia/Seoul")
    public void generateDailyAiInfo() {
        log.info("---[SimSimSchedule] 스케줄링 작업 시작---");
        aiBatchService.generateDailyAiInfo();
        log.info("---[SimSimSchedule] 스케줄링 작업 종료---");
    }
}
