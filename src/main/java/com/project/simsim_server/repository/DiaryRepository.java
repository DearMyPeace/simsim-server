package com.project.simsim_server.repository;

import com.project.simsim_server.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
