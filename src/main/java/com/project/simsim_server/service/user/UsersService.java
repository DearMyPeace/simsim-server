package com.project.simsim_server.service.user;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Grade;
import com.project.simsim_server.domain.user.Persona;
import com.project.simsim_server.domain.user.Reply;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.dto.user.UserInfoResponseDTO;
import com.project.simsim_server.exception.ResourceNotFoundException;
import com.project.simsim_server.exception.UserNotFoundException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.PersonaRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PersonaRepository personaRepository;
    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository aiInfoRepository;


    public PersonaResponseDTO updatePersona(String personaCode, Long userId) {
        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UserNotFoundException("[페르소나 변경 에러] 해당 유저를 찾을 수 없습니다.",
                    "USER_NOT_FOUND");

        Users userData = user.get();
        String changeCode = userData.updatePersona(personaCode);
        usersRepository.save(userData);

        Optional<Persona> data = personaRepository.findByPersonaCode(changeCode);
        if (data.isEmpty()) {
            throw new ResourceNotFoundException("[페르소나 변경 에러] 페르소나 정보를 찾을 수 없습니다. 검색 코드 : " + changeCode,
                    "RESOURCE_NOT_FOUND");
        }
        return new PersonaResponseDTO(data.get());
    }


//    public String updateBgImg(String bgimg, Long userId) {
//        Optional<Users> user = usersRepository.findById(userId);
//        if (user.isEmpty())
//            throw new UserNotFoundException("[배경화면 변경 에러] 해당 유저를 찾을 수 없습니다.",
//                    "USER_NOT_FOUND");
//        Users userData = user.get();
//        userData.updateBgImg(bgimg);
//        usersRepository.save(userData);
//        return userData.getBgImage();
//    }


    public UserInfoResponseDTO findByUserId(Long userId) {
        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UserNotFoundException("[회원 조회 에러] 해당 유저를 찾을 수 없습니다.",
                    "USER_NOT_FOUND");
        Users userData = user.get();

        Reply replyStatus = Reply.DEFAULT;

        List<DailyAiInfo> notReadAiReply = aiInfoRepository.findByUserIdAndReplyStatus(userId);
        if (!notReadAiReply.isEmpty()) {
            replyStatus = Reply.RECEIVE;
            //TODO - 추후 Grade에 따른 분기 추가
        }


        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Diary> diaries = diaryRepository.findDiariesByCreatedDateAndUserId(yesterday, userId);
        if (!diaries.isEmpty()) {
            aiInfoRepository.findByCreatedAtBeforeAndUserId(userId, LocalDate.now());
        }

        Optional<Persona> persona = personaRepository.findByPersonaCode(userData.getPersona());
        if (persona.isEmpty())
            throw new ResourceNotFoundException("[회원 조회 에러] 페르소나 정보가 없습니다", "RESOURCE_NOT_FOUND");

        String personaName = persona.get().getPersonaName();
        return new UserInfoResponseDTO(userData, replyStatus.getKey(), personaName);
    }
}
