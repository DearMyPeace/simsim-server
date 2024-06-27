package com.project.simsim_server.service.setting;

import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsersService {
    
    private final UsersRepository usersRepository;

    public String updatePersona(String personaCode, Long userId) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty())
            throw new RuntimeException("[페르소나 변경 에러] 해당 회원을 찾을 수 없습니다.");

        Users userData = user.get();
        userData.updatePersona(personaCode);
        usersRepository.save(userData);
        return userData.getPersona();
    }

    public String updateBgImg(String bgimg, Long userId) {
        Optional<Users> user = usersRepository.findById(userId);
        if (user.isEmpty())
            throw new RuntimeException("[배경화면 변경 에러] 해당 회원을 찾을 수 없습니다.");
        Users userData = user.get();
        userData.updateBgImg(bgimg);
        usersRepository.save(userData);
        return userData.getBgImage();
    }
}
