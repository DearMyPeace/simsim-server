package com.project.simsim_server.controller.user;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.dto.user.PersonaResponseDTO;
import com.project.simsim_server.dto.user.UserInfoResponseDTO;
import com.project.simsim_server.service.user.UsersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "회원정보 서비스")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
public class UsersController {

    private final UsersService userService;
    private final AuthenticationService authenticationService;

    /**
     * 회원 정보 조회
     * @return UserInfoResponseDTO 회원 정보
     */
    @GetMapping("/me")
    public UserInfoResponseDTO getUserInfo() {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return userService.findByUserId(userId);
    }

    /**
     * 세팅에서 페르소나 변경 시 회원 정보에 반영
     * @param personaCode
     * @return PersonaResponseDTO 변경 후 페르소나 코드, 페르소나 명칭
     */
    @PatchMapping("/persona/{personaCode}")
    public PersonaResponseDTO changeUserPersona(@PathVariable String personaCode) {
        Long userId = authenticationService.getUserIdFromAuthentication();
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
}
