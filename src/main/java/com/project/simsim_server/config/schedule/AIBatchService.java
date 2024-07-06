package com.project.simsim_server.config.schedule;

//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class AIBatchService {
//
//    private AIService aiService;
//    private UsersRepository usersRepository;
//
//    @Transactional
//    public void saveAuto() {
//        /**
//         * 모든 회원의 전날 일기에 대한 AI 응답 생성 로직
//         */
//        // 모든 회원 조회
////        List<Users> userList = usersRepository.findAllAndUserStatus();
//        // 전날 일기 목록 조회
////        LocalDate targetDate = LocalDate.now().minusDays(1);
////        LocalDateTime startDateTime = LocalDate.now().minusDays(1).atStartOfDay();
////        LocalDateTime endDateTime = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
//
////        for (Users user : userList) {
////            try {
////                processUser(user, targetDate, startDateTime, endDateTime);
////            } catch (Exception e) {
////                log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
////            }
////        }
//
//        /**
//         * 테스트용 로직 : 5번 유저의 6월치 분석 배치
//         */
//        Long testId = 5L;
//        Optional<Users> usersOptional = usersRepository.findById(testId);
//        Users user = usersOptional.get();
//        LocalDate targetDate = LocalDate.of(2024, 6, 1);
//        for (int i = 1; i <= 30; i++){
//            LocalDateTime startDateTime = targetDate.atStartOfDay();
//            LocalDateTime endDateTime = targetDate.atTime(LocalTime.MAX);
//            try {
//                aiService.processUser(user, targetDate, startDateTime, endDateTime);
//                targetDate = targetDate.plusDays(1);
//            } catch (Exception e) {
//                log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
//            }
//        }
//    }
//}
