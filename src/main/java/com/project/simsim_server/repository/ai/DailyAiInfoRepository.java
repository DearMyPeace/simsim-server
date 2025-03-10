package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.dto.ai.client.AnalyzeMaxInfoDTO;
import com.project.simsim_server.dto.ai.client.WeekEmotionsResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DailyAiInfoRepository extends JpaRepository<DailyAiInfo, Long> {

    List<DailyAiInfo> findByUserId(Long userId);

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
    List<DailyAiInfo> findByCreatedAtAndUserId(
        @Param("userId") Long userId,
        @Param("targetDate") LocalDate targetDate
    );

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.targetDate BETWEEN :startDate AND :endDate")
    List<DailyAiInfo> findAllByIdAndTargetDate(
            @Param("userId") Long userId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId AND dr.replyStatus = 'N' ORDER BY dr.targetDate DESC")
    List<DailyAiInfo> findByUserIdAndReplyStatus(Long userId);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId AND dr.targetDate BETWEEN :startDate AND :endDate")
    List<DailyAiInfo> findAllSummaryByDate(LocalDate startDate, LocalDate endDate, Long userId);

    Optional<DailyAiInfo> findByAiIdAndUserId(Long id, Long userId);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.replyStatus ='F' AND dr.isFirst = true")
    List<DailyAiInfo> findByFirstReply();
}
