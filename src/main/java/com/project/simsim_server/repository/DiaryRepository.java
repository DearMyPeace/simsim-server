package com.project.simsim_server.repository;

import com.project.simsim_server.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface DiaryRepository extends JpaRepository<Diary, Long> {
    public List<Diary> findDiariesByCreatedDateAndUserPk(LocalDateTime createdDate, Long UserPk);
}
