package com.project.simsim_server.service.admin;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExportService {

    private final UsersRepository usersRepository;
    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;

    public void getDiaries(Long userId, String fileName) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("해당 유저는 접근할 수 없습니다.");
        }

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

    public void getReponses(Long userId, String fileName) throws IOException {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("해당 유저는 접근할 수 없습니다.");
        }

        List<DailyAiInfo> responses = dailyAiInfoRepository.findAll();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("ai_id,user_id,ai_target_date,ai_diary_summary,ai_reply_content,ai_reply_status,created_date,modified_date\n");
            for (DailyAiInfo reponse : responses) {
                writer.append(String.format(
                        "%d,%d,%s,%s,%s,%s,%s,%s\n",
                        reponse.getAiId(),
                        reponse.getUserId(),
                        reponse.getTargetDate(),
                        reponse.getDiarySummary(),
                        reponse.getReplyContent(),
                        reponse.getReplyStatus(),
                        reponse.getCreatedDate(),
                        reponse.getModifiedDate()
                ));
            }
        }
    }
}
