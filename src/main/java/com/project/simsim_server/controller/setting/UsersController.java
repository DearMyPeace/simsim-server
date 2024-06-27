package com.project.simsim_server.controller.setting;

import com.project.simsim_server.service.setting.UsersService;
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

    @PatchMapping("/persona/{personaCode}/{userId}")
    public String changeUserPersona(@PathVariable String personaCode,
        @PathVariable String userId //TODO - 인증완료 시 삭제
    ) {
//        String userId = getUserIdFromAuthentication();
        return userService.updatePersona(personaCode, Long.parseLong(userId));
    }

    @PatchMapping("/bgimg")
    public String changeBackgroundImage(@RequestBody String bgimg) {
        String userId = getUserIdFromAuthentication();
        return userService.updateBgImg(bgimg, Long.parseLong(userId));
    }

    private String getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
