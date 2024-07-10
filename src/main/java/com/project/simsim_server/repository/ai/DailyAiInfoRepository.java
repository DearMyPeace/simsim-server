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

    List<DailyAiInfo> findByUserId(Long userId);


    @Query("SELECT new com.project.simsim_server.dto.ai.client.WeekEmotionsResponseDTO(" +
            "SUM(dr.happyCnt), " +
            "SUM(dr.appreciationCnt), " +
            "SUM(dr.loveCnt), " +
            "SUM(dr.analyzePositiveTotal), " +
            "SUM(dr.tranquilityCnt), " +
            "SUM(dr.curiosityCnt), " +
            "SUM(dr.surpriseCnt), " +
            "SUM(dr.analyzeNeutralTotal), " +
            "SUM(dr.sadCnt)," +
            "SUM(dr.angryCnt)," +
            "SUM(dr.fearCnt)," +
            "SUM(dr.analyzeNegativeTotal)) " +
            "FROM DailyAiInfo dr WHERE dr.userId =:userId AND dr.targetDate BETWEEN :startDate AND :endDate")
    Optional<WeekEmotionsResponseDTO> countByUserIdAndTargetDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT d.aiId, d.targetDate, MAX(d.analyzePositiveTotal) FROM DailyAiInfo d WHERE d.userId = :userId AND d.targetDate BETWEEN :startDate AND :endDate AND d.analyzePositiveTotal = " +
            "(SELECT MAX(d2.analyzePositiveTotal) FROM DailyAiInfo d2 WHERE d2.userId = d.userId AND d2.targetDate BETWEEN :startDate AND :endDate)")
    List<AnalyzeMaxInfoDTO> findAllByUserIdAndAnalyzePositiveTotal(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT d.aiId, d.targetDate, MAX(d.analyzeNeutralTotal) FROM DailyAiInfo d WHERE d.userId = :userId AND d.targetDate BETWEEN :startDate AND :endDate AND d.analyzeNeutralTotal = " +
            "(SELECT MAX(d2.analyzeNeutralTotal) FROM DailyAiInfo d2 WHERE d2.userId = d.userId AND d2.targetDate BETWEEN :startDate AND :endDate)")
    List<AnalyzeMaxInfoDTO> findAllByUserIdAndAnalyzeNeutralTotal(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT d.aiId, d.targetDate, MAX(d.analyzeNegativeTotal) FROM DailyAiInfo d WHERE d.userId = :userId AND d.targetDate BETWEEN :startDate AND :endDate AND d.analyzeNegativeTotal = " +
            "(SELECT MAX(d2.analyzeNegativeTotal) FROM DailyAiInfo d2 WHERE d2.userId = d.userId AND d2.targetDate BETWEEN :startDate AND :endDate)")
    List<AnalyzeMaxInfoDTO> findAllByUserIdAndAnalyzeNegativeTotal(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.replyStatus ='F' AND dr.isFirst = true")
    List<DailyAiInfo> findByFirstReply();
}
