package com.project.simsim_server.repository.diary;

import com.project.simsim_server.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface DiaryRepository extends JpaRepository<Diary, Long> {
    public List<Diary> findDiariesByCreatedDateAndUserId(LocalDateTime createdDate, Long userId);

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId " +
            "AND d.diaryDeleteYn = 'N'" +
            "AND d.createdDate >= :startDate AND d.createdDate < :endDate")
    List<Diary> findDiariesByCreatedAtBetweenAndUserId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT d.createdDate, COUNT(*) FROM Diary d " +
            "WHERE d.userId = :userId " +
            "AND d.diaryDeleteYn = 'N' " +
            "AND d.createdDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.createdDate")
    List<Object[]> countDiariesByDate(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId
    );
}
