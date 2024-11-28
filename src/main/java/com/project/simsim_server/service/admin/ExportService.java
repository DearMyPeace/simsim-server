package com.project.simsim_server.service.admin;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Role;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExportService {

    private final UsersRepository usersRepository;
    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;

    public List<Map<String, Object>> getDiaries(Long userId, String fileName) {
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
        } catch (IOException e) {
            log.error("--- [SimSimError] 기록 CSV 파일 생성 실패: {}", fileName, e);
        }

        return allDiaries.stream().map((diary -> {
            Map<String, Object> diaryMap = new LinkedHashMap<>();
            diaryMap.put("diaryId", diary.getDiaryId());
            diaryMap.put("userId", diary.getUserId());
            diaryMap.put("content", diary.getContent());
            diaryMap.put("listKey", diary.getListKey());
            diaryMap.put("diaryDeleteYn", diary.getDiaryDeleteYn());
            diaryMap.put("markedDate", diary.getMarkedDate());
            diaryMap.put("createdDate", diary.getCreatedDate());
            diaryMap.put("modifiedDate", diary.getModifiedDate());
            diaryMap.put("sendAble", diary.getSendAble());
            return diaryMap;
        })).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getReponses(Long userId, String fileName) {
        Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("해당 회원이 존재하지 않습니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("해당 유저는 접근할 수 없습니다.");
        }

        List<DailyAiInfo> responses = dailyAiInfoRepository.findAll();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("ai_id,user_id,ai_target_date,ai_diary_summary,ai_reply_content,ai_reply_status,created_date,modified_date\n");
            for (DailyAiInfo reponse : responses) {
                writer.append(String.format(
                        "%d,%d,%s,\"%s\",\"%s\",%s,%s,%s\n",
                        reponse.getAiId(),
                        reponse.getUserId(),
                        reponse.getTargetDate(),
                        reponse.getDiarySummary().replace("\"", "\"\""),
                        reponse.getReplyContent().replace("\"", "\"\""),
                        reponse.getReplyStatus(),
                        reponse.getCreatedDate(),
                        reponse.getModifiedDate()
                ));
            }
        } catch (IOException e) {
            log.error("--- [SimSimError] 편지 CSV 파일 생성 실패: {}", fileName, e);
        }

        return responses.stream().map((response) -> {
            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("ai_id", response.getAiId());
            responseMap.put("user_id", response.getUserId());
            responseMap.put("ai_target_date", response.getTargetDate());
            responseMap.put("ai_diary_summary", escapeJson(response.getDiarySummary()));
            responseMap.put("ai_reply_content", escapeJson(response.getReplyContent()));
            responseMap.put("ai_reply_status", response.getReplyStatus());
            responseMap.put("created_date", response.getCreatedDate());
            responseMap.put("modified_date", response.getModifiedDate());
            return responseMap;
        }).collect(Collectors.toList());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

}
