package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
    @Query("SELECT mr FROM MonthlyReport mr " +
            "WHERE mr.userId = :userId " +
            "AND mr.targetYear = :targetYear " +
            "AND mr.targetMonth = :targetMonth " +
            "ORDER BY mr.monthReportId DESC")
    List<MonthlyReport> findByIdAndTargetDate(
            @Param("userId") Long userId,
            @Param("targetYear") int targetYear,
            @Param("targetMonth") int targetMonth);

}
