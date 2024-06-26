package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DailyAiInfoRepository extends JpaRepository<DailyAiInfo, Long> {

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.createdDate <= :targetDateTime " +
            "ORDER BY dr.createdDate DESC")
    public List<DailyAiInfo> findTopNByCreatedAtBeforeAndUserId(
            @Param("userId") Long userId,
            @Param("targetDate") LocalDateTime targetDateTime,
            Pageable pageable
    );

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.userId =:userId " +
            "AND dr.targetDate =:targetDate")
    public List<DailyAiInfo> findByCreatedAtBeforeAndUserId(
        @Param("userId") Long userId,
        @Param("targetDate") LocalDate targetDate
    );
}
