package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyAiInfoRepository extends JpaRepository<DailyAiInfo, Long> {

    @Query("SELECT dr FROM DailyAiInfo dr WHERE dr.createdDate <= :targetDate AND dr.userId =:userId ORDER BY dr.createdDate DESC")
    public List<DailyAiInfo> findTopNByCreatedAtBeforeAndUserId(
                                                    @Param("userId") Long userId,
                                                    @Param("targetDate") LocalDateTime targetDate,
                                                    Pageable pageable
    );
}
