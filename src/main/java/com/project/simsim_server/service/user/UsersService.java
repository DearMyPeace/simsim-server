package com.project.simsim_server.service.user;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.user.Persona;
import com.project.simsim_server.domain.user.Reply;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.dto.user.UserInfoResponseDTO;
import com.project.simsim_server.exception.ResourceNotFoundException;
import com.project.simsim_server.exception.user.UsersException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.user.PersonaRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.project.simsim_server.exception.user.UsersErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PersonaRepository personaRepository;
    private final DailyAiInfoRepository aiInfoRepository;


    public UserInfoResponseDTO findByUserId(Long userId) {
        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UsersException(USER_NOT_FOUND);
        Users userData = user.get();

        Reply replyStatus = Reply.DEFAULT;
        List<DailyAiInfo> notReadAiReply = aiInfoRepository.findByUserIdAndReplyStatus(userId);
        if (!notReadAiReply.isEmpty()) {
            // 읽지 않은 편지가 있는 경우
            replyStatus = Reply.RECEIVE;
            // TODO - 추후 Grade에 따른 분기 추가
        } else {
            List<DailyAiInfo> allReplies
                    = Optional.ofNullable(aiInfoRepository.findByUserId(userId)).orElse(Collections.emptyList());
            if (!allReplies.isEmpty()) { // 편지가 있으면서 전부 다 읽은 경우
                replyStatus = Reply.CHECK;
            }
        }

        Optional<Persona> persona = personaRepository.findByPersonaCode(userData.getPersona());
        if (persona.isEmpty())
            throw new ResourceNotFoundException("[회원 조회 에러] 페르소나 정보가 없습니다", "RESOURCE_NOT_FOUND");

        String personaName = persona.get().getPersonaName();
        return new UserInfoResponseDTO(userData, replyStatus.getKey(), personaName);
    }


    public PersonaResponseDTO updatePersona(String personaCode, Long userId) {
        Optional<Users> user = usersRepository.findByIdAndUserStatus(userId);
        if (user.isEmpty())
            throw new UsersException(USER_NOT_FOUND);

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
//            throw new UsersException(USER_NOT_FOUND);
//        Users userData = user.get();
//        userData.updateBgImg(bgimg);
//        usersRepository.save(userData);
//        return userData.getBgImage();
//    }
}
