package com.project.simsim_server.controller;

import com.project.simsim_server.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//TODO - 추후 프론트엔드와 연결 후 수정
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final HttpSession httpSession;

    @GetMapping("/")
    public String index() {
        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
        if (sessionUser != null) {
//            return sessionUser.getName();
            return "/html/loginsuccess.html";
        }
        return "index.html";
    }

}
