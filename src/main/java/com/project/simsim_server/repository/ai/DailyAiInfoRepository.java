package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyAiInfoRepository extends JpaRepository<DailyAiInfo, Long> {

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.createdDate <= :targetDateTime " +
            "ORDER BY dr.createdDate DESC")
    List<DailyAiInfo> findTopNByCreatedAtBeforeAndUserId(
            @Param("userId") Long userId,
            @Param("targetDateTime") LocalDateTime targetDateTime,
            Pageable pageable
    );

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.targetDate =:targetDate")
    List<DailyAiInfo> findByCreatedAtBeforeAndUserId(
        @Param("userId") Long userId,
        @Param("targetDate") LocalDate targetDate
    );

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.targetDate BETWEEN :startDate AND :endDate")
    List<DailyAiInfo> findAllByIdAndTargetDate(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId = :userId ORDER BY dr.targetDate DESC")
    List<DailyAiInfo> findTopByUserIdOrderByTargetDateDesc(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId = :userId AND dr.replyStatus = 'N' ORDER BY dr.targetDate DESC")
    List<DailyAiInfo> findByUserIdAndReplyStatus(Long userId);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId = :userId AND dr.targetDate BETWEEN :startDate AND :endDate")
    List<DailyAiInfo> findAllSummaryByDate(LocalDate startDate, LocalDate endDate, Long userId);

    Optional<DailyAiInfo> findByAiIdAndUserId(Long id, Long userId);
}
