//package com.project.simsim_server.repository.diary;
//
//import com.project.simsim_server.config.encrytion.EncryptionUtil;
//import com.project.simsim_server.domain.diary.Diary;
//import com.project.simsim_server.dto.diary.DiaryResponseDTO;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//class DiaryRepositoryTest {
//
//    @Autowired
//    private DiaryRepository diaryRepository;
//
//    @Autowired
//    private EncryptionUtil encryptionUtil;
//
//    @Test
//    void 일기내용_암호화() {
//        // given
//        String testContent = "일기 내용 암호화 테스트";
//        Diary diary
//                = Diary.builder()
//                .userId(1L)
//                .content(testContent)
//                .createdDate(LocalDateTime.now())
//                .modifiedDate(LocalDateTime.now())
//                .build();
//
//        // when
//        Diary save = diaryRepository.save(diary);
//
//        // then
//        assertThat(save.getContent()).isEqualTo(testContent);
//        System.out.println("save.getContent() = " + save.getContent());
//    }
//
//    @Test
//    void 기존저장일기_암호화() {
//        // given
//        Optional<Diary> data = diaryRepository.findById(3L);
//
//        // when
//        String content = null;
//        if (data.isPresent()) {
//            Diary diary = data.get();
//            content = diary.getContent();
//            System.out.println("diary.getContent() = " + diary.getContent());
//            diary.update(diary.getContent(), LocalDateTime.now());
//            diaryRepository.save(diary);
//        }
//
//        // then
//        Diary diaryResult = diaryRepository.findById(3L).get();
//        assertThat(diaryResult.getContent()).isEqualTo(content);
//    }
//
//    @Test
//    void 암호화문_프론트전달() throws Exception {
//        // given
//        Optional<Diary> data = diaryRepository.findById(3L);
//        String originContent = "요새는 배가 별로 안 고픈데 왜 그럴까요? 그냥 먹고 졸린게 싫어요";
//        DiaryResponseDTO responseDTO = null;
//
//        // when
//        if (data.isPresent()) {
//            responseDTO = new DiaryResponseDTO(data.get());
//        }
//
//        // then
//        assertThat(responseDTO).isNotNull();
//        assertThat(responseDTO.getContent()).isEqualTo(originContent);
//        System.out.println("responseDTO.getContent() = " + responseDTO.getContent());
//    }
//}