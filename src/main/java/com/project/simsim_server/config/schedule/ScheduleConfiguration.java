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
    private final UsersMigrationRunner usersMigrationRunner;
    private final DiaryMigrationRunner diaryMigrationRunner;

    @Async
    @Scheduled(cron = "0 30 2 * * ?", zone = "Asia/Seoul")
    public void generateDailyAIReply() {
        log.info("---[SimSimSchedule] 스케줄링 작업 시작---");
        aiBatchService.deleteFirstReply();
        log.info("---[SimSimSchedule] 스케줄링 작업 종료---");
    }

//    @Async
//    @Scheduled(cron = "0 53 20 * * ?", zone = "Asia/Seoul") // 매일 새벽 3시에 실행
//    public void migrateData() {
//        log.info("---[SimSimSchedule] 데이터 마이그레이션 작업 시작---");
//        try {
//            usersMigrationRunner.migrate();
//        } catch (Exception e) {
//            log.error("데이터 마이그레이션 작업 중 오류 발생: ", e);
//        }
//        log.info("---[SimSimSchedule] 데이터 마이그레이션 작업 종료---");
//    }

    @Async
    @Scheduled(cron = "0 34 22 * * ?", zone = "Asia/Seoul") // 매일 22:20에 실행
    public void migrateData() {
        log.info("---[SimSimSchedule] 데이터 마이그레이션 작업 시작---");
        try {
            diaryMigrationRunner.encryptAndSaveAllEntries();
        } catch (Exception e) {
            log.error("데이터 마이그레이션 작업 중 오류 발생: ", e);
        }
        log.info("---[SimSimSchedule] 데이터 마이그레이션 작업 종료---");
    }
}
