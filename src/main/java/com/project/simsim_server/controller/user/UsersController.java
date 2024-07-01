package com.project.simsim_server.controller.user;

import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.dto.user.UserInfoResponseDTO;
import com.project.simsim_server.service.user.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UsersController {

    private final UsersService userService;

    /**
     * 회원 정보 조회
     * @return UserInfoResponseDTO 회원 정보
     */
    @GetMapping("/me/{num}")
    public UserInfoResponseDTO getUserInfo(@PathVariable int num) {
        String authentication = getUserIdFromAuthentication();
        log.warn("-------조회된 인증 객체 정보: {}", authentication);
        Long userId = Long.parseLong(authentication);
        log.info("[/me API]authentication: {}", authentication);
        return userService.findByUserId(userId);
    }

    /**
     * 세팅에서 페르소나 변경 시 회원 정보에 반영
     * @param personaCode
     * @return PersonaResponseDTO 변경 후 페르소나 코드, 페르소나 명칭
     */
    @PatchMapping("/persona/{personaCode}")
    public PersonaResponseDTO changeUserPersona(@PathVariable String personaCode)
    {
        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        return userService.updatePersona(personaCode, userId);
    }


    /**
     * 세팅에서 배경화면 변경 시 회원 정보에 반영
     * @param
     * @return String 변경 후 배경화면 경로
     */
//    @PatchMapping("/bgimg")
//    public String changeBackgroundImage(@RequestBody String bgimg) {
//        String userId = getUserIdFromAuthentication();
//        return userService.updateBgImg(bgimg, Long.parseLong(userId));
//    }



    private String getUserIdFromAuthentication() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        log.warn("조회할 객체 : {}", authentication);
        return authentication.getName();
    }
}
