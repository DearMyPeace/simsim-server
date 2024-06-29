package com.project.simsim_server.controller.user;

import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.dto.user.UserInfoResponseDTO;
import com.project.simsim_server.service.user.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@CrossOrigin(origins = "*")
@RestController
public class UsersController {

    private final UsersService userService;

    /**
     * 회원 정보 조회
     * @return UserInfoResponseDTO 회원 정보
     */
    @GetMapping("/me/{userId}") //TODO - userId 추후 제거
    public UserInfoResponseDTO getUserInfo(@PathVariable("userId") String authentication) {
//        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        return userService.findByUserId(userId);
    }

    /**
     * 세팅에서 페르소나 변경 시 회원 정보에 반영
     * @param personaCode
     * @return PersonaResponseDTO 변경 후 페르소나 코드, 페르소나 명칭
     */
    @PatchMapping("/persona/{personaCode}") //TODO - userId 추후 제거
    public PersonaResponseDTO changeUserPersona(
            @PathVariable String personaCode,
            @RequestBody Long userId)
    {
//        String authentication = getUserIdFromAuthentication();
//        Long userId = Long.parseLong(authentication);
        return userService.updatePersona(personaCode, userId);
    }


    /**
     * 세팅에서 배경화면 변경 시 회원 정보에 반영
     * @param bgimg
     * @return String 변경 후 배경화면 경로
     */
//    @PatchMapping("/bgimg")
//    public String changeBackgroundImage(@RequestBody String bgimg) {
//        String userId = getUserIdFromAuthentication();
//        return userService.updateBgImg(bgimg, Long.parseLong(userId));
//    }



    private String getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
