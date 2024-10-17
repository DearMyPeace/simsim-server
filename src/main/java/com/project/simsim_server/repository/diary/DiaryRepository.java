package com.project.simsim_server.repository.diary;

import com.project.simsim_server.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId " +
            "AND d.diaryDeleteYn = 'N'" +
            "AND d.createdDate BETWEEN :startDate AND :endDate")
    List<Diary> findDiariesByCreatedAtBetweenAndUserId(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT d.markedDate, COUNT(*) FROM Diary d " +
            "WHERE d.userId = :userId " +
            "AND d.diaryDeleteYn = 'N' " +
            "AND d.markedDate BETWEEN :startDate AND :endDate " +
            "GROUP BY d.markedDate")
    List<Object[]> countDiariesByDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId
    );

    @Query("SELECT d FROM Diary d WHERE d.diaryId = :diaryId " +
            "AND d.userId =:userId")
    Optional<Diary> findByIdAndUserId(Long diaryId, Long userId);

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId " +
            "AND d.diaryDeleteYn = 'N'" +
            "AND d.markedDate = :targetDate")
    List<Diary> findByCreatedAtAndUserId(Long userId, LocalDate targetDate);

    @Query("SELECT d FROM Diary d WHERE d.userId = :userId " +
            "AND d.markedDate = :targetDate")
    List<Diary> findAllByCreatedAtAndUserId(Long userId, LocalDate targetDate);

    List<Diary> findByUserId(@Param("userId") Long userId);
}
