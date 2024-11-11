package com.project.simsim_server.repository.ai;

import com.project.simsim_server.domain.ai.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
}
