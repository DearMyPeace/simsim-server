package com.project.simsim_server.service.admin;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExportService {

    private final DiaryRepository diaryRepository;

    public void getDiaries(Long userId, String fileName) throws IOException {

        //if (userId) {}

        List<Diary> allDiaries = diaryRepository.findAll();

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append("diary_id,user_id,diary_content,diary_list_key,diary_delete_yn,marked_date,created_date,modified_date,is_send_able\n");

            for (Diary diary : allDiaries) {
                writer.append(String.format(
                        "%d,%d,\"%s\",%s,%s,%s,%s,%s,%s\n",
                        diary.getDiaryId(),
                        diary.getUserId(),
                        diary.getContent(),
                        diary.getListKey(),
                        diary.getDiaryDeleteYn(),
                        diary.getMarkedDate(),
                        diary.getCreatedDate(),
                        diary.getModifiedDate(),
                        diary.getSendAble()
                ));
            }
        }
    }
}
