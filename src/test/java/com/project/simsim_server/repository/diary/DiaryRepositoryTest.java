package com.project.simsim_server.repository.diary;

import com.project.simsim_server.domain.diary.Diary;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DiaryRepositoryTest {

    @Autowired
    DiaryRepository diaryRepository;

    @AfterEach
    public void cleanUp(){
        diaryRepository.deleteAll();
    }

    @Test
    public void 일기작성_불러오기() {
        //given
        Long userId = 8L;
        String content = "Hello World";

        diaryRepository.save(Diary.builder()
                .userId(userId)
                .content(content)
                .build()
        );

        //when
        List<Diary> diaryList = diaryRepository.findAll();

        //then
        Diary firstDiary = diaryList.getFirst();
        Assertions.assertThat(firstDiary.getContent()).isEqualTo(content);
    }
}