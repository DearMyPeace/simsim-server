//package com.project.simsim_server.controller.diary;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class DiaryControllerTest {
//    @Autowired
//    private TestRestTemplate testTemplate;
//
//    @LocalServerPort
//    private int port;
//
//    @Test
//    void updateDiary() {
//        String url = "http://localhost:" + port + "/api/v1/aiLetters/save";
//    }
//
//    @Test
//    void deleteDiary() {
//    }
//}