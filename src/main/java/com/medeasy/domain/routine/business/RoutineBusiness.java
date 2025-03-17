package com.medeasy.domain.routine.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.ai.dto.AiResponseDto;
import com.medeasy.domain.ai.service.AiService;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.medicine.service.MedicineService;
import com.medeasy.domain.ocr.dto.OcrParsedDto;
import com.medeasy.domain.ocr.service.OcrServiceByMultipart;
import com.medeasy.domain.routine.converter.RoutineConverter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.*;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.routine_medicine.service.RoutineMedicineService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Business
public class RoutineBusiness {
    private final RoutineService routineService;
    private final UserService userService;
    private final MedicineService medicineService;
    private final MedicineDocumentService medicineDocumentService;
    private final RoutineConverter routineConverter;
    private final OcrServiceByMultipart ocrService;
    private final AiService aiService;
    private final MedicineConverter medicineConverter;
    private final StringRedisTemplate redisAlarmTemplate;
    private final ObjectMapper objectMapper;

    private final UserScheduleService userScheduleService;
    private final RoutineMedicineService routineMedicineService;
    private final UserScheduleConverter userScheduleConverter;

    // 생성자 주입 + @Qualifier 적용
    public RoutineBusiness(
            RoutineService routineService,
            UserService userService,
            MedicineService medicineService,
            MedicineDocumentService medicineDocumentService,
            RoutineConverter routineConverter,
            OcrServiceByMultipart ocrService,
            AiService aiService,
            MedicineConverter medicineConverter,
            @Qualifier("redisTemplateForAlarm") StringRedisTemplate redisAlarmTemplate, // @Qualifier 적용
            ObjectMapper objectMapper,
            UserScheduleService userScheduleService,
            RoutineMedicineService routineMedicineService,
            UserScheduleConverter userScheduleConverter) {
        this.routineService = routineService;
        this.userService = userService;
        this.medicineService = medicineService;
        this.medicineDocumentService = medicineDocumentService;
        this.routineConverter = routineConverter;
        this.ocrService = ocrService;
        this.aiService = aiService;
        this.medicineConverter = medicineConverter;
        this.redisAlarmTemplate = redisAlarmTemplate;
        this.objectMapper = objectMapper;
        this.userScheduleService = userScheduleService;
        this.routineMedicineService = routineMedicineService;
        this.userScheduleConverter = userScheduleConverter;
    }
    /**
     * 단일 약 루틴 저장
     *
     * 3/16 업데이트
     * */
    @Transactional
    public void registerRoutine(Long userId, RoutineRegisterRequest routineRegisterRequest) {
        // Entity 값 가져오기
        UserEntity userEntity = userService.getUserById(userId); // 1번
        MedicineDocument medicineDocument = medicineDocumentService.findMedicineDocumentById(routineRegisterRequest.getMedicineId());
        List<UserScheduleEntity> userScheduleEntities=userScheduleService.findAllByIdInOrderByTakeTimeAsc(routineRegisterRequest.getUserScheduleIds());

        String nickname=routineRegisterRequest.getNickname() == null ? medicineDocument.getItemName() : routineRegisterRequest.getNickname();
        int dose = routineRegisterRequest.getDose();

        List<RoutineMedicineEntity> routineMedicineEntities=new ArrayList<>();
        int quantity=0;

        // 오늘 날짜의 복용 루틴 저장
        LocalDate currentDate = LocalDate.now();
        List<LocalDate> routineDates=calculateRoutineDates(routineRegisterRequest);

        log.info("약을 복용하는 날짜 결과: {}", routineDates.toString());

        if(routineDates.contains(currentDate)) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                if (LocalTime.now().isAfter(userScheduleEntity.getTakeTime())) {
                    continue;
                }
                quantity += dose;

                if (quantity > routineRegisterRequest.getTotalQuantity()) break;

                RoutineEntity routineEntity=routineService.getRoutineByUserScheduleAndTakeDate(userEntity, userScheduleEntity, currentDate);

                RoutineMedicineEntity routineMedicineEntity=RoutineMedicineEntity.builder()
                        .nickname(nickname)
                        .isTaken(false)
                        .dose(dose)
                        .routine(routineEntity)
                        .medicineId(medicineDocument.getId())
                        .build()
                        ;

                routineMedicineEntities.add(routineMedicineEntity);
            }

            routineDates.remove(currentDate);
        }

        for (LocalDate localDate : routineDates) {
            for (UserScheduleEntity userScheduleEntity : userScheduleEntities) {
                quantity += dose;
                if (quantity > routineRegisterRequest.getTotalQuantity()) break;

                // routine entity 가 존재한다면 가져오기 아니면 생성하기
                RoutineEntity routineEntity=routineService.getRoutineByUserScheduleAndTakeDate(userEntity, userScheduleEntity, localDate);

                RoutineMedicineEntity routineMedicineEntity=RoutineMedicineEntity.builder()
                        .nickname(nickname)
                        .isTaken(false)
                        .dose(dose)
                        .routine(routineEntity)
                        .medicineId(medicineDocument.getId())
                        .build()
                        ;

                routineMedicineEntities.add(routineMedicineEntity);
            }
        }
        log.info("단일 약 루틴 저장");
        routineMedicineService.saveAll(routineMedicineEntities);
    }

    public void registerRoutineList(Long userId, List<RoutineRegisterRequest> routinesRegisterRequest) {
        routinesRegisterRequest.forEach(routineRegisterRequest -> {
            registerRoutine(userId, routineRegisterRequest);
        });
    }

    /**
     * 3/16
     * 약을 복용할 날짜 구하기
     * */
    public List<LocalDate> calculateRoutineDates(RoutineRegisterRequest routineRegisterRequest) {
        int dailyDose=routineRegisterRequest.getUserScheduleIds().size()*routineRegisterRequest.getDose();

        int requiredDays=(int) Math.ceil((double) routineRegisterRequest.getTotalQuantity()/dailyDose); // 반올림

        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        while(dates.size() < requiredDays) {
            int todayDayValue = currentDate.getDayOfWeek().getValue();

            if(routineRegisterRequest.getDayOfWeeks().contains(todayDayValue)) {
                dates.add(currentDate);
            }

            currentDate = currentDate.plusDays(1);
        }

        return dates;
    }

    /**
     * 시작 날짜와 끝나는 날짜 범위에 해당하는
     * 루틴 스케줄을 조회
     * */
    public List<RoutineGroupDto> getRoutineListByDate(Long userId, LocalDate startDate, LocalDate endDate) {
        UserEntity userEntity = userService.getUserById(userId);
        List<RoutineGroupDto> routineGroupDtos=routineService.getRoutinesByDatesAndUserID(userId, startDate, endDate);

        return routineGroupDtos;
    }

    /**
     * 루틴 복용 체크 메서드
     * */
    @Transactional()
    public RoutineCheckResponse checkRoutine(Long routineMedicineId, Boolean isTaken) {

        var routineMedicineEntity=routineMedicineService.findById(routineMedicineId);
        Boolean beforeTaken=routineMedicineEntity.getIsTaken();

        routineMedicineEntity.setIsTaken(isTaken);

        return RoutineCheckResponse.builder()
                .routineMedicineId(routineMedicineId)
                .afterIsTaken(isTaken)
                .beforeIsTaken(beforeTaken)
                .build()
                ;
    }

    /**
     * 처방전 루틴 등록 메서드
     *
     * 처방전 분석 데이터를 토대로 루틴 추가 여부 응답을 전송한다.
     * */
    public List<RoutinePrescriptionResponse> registerRoutineByPrescription(Long userId, MultipartFile file) {
        // TODO fetch join으로 User Schedule도 가져오기
        var userEntity = userService.getUserByIdToFetchJoin(userId);
        var userSchedules=userEntity.getUserSchedules().stream().map(userScheduleConverter::toDto).toList();

        // 처방전 이미지 파싱
        List<OcrParsedDto> parseData=ocrService.sendOcrRequest(file);
        log.info("처방전 이미지 ocr 분석 완료");

        // ai api를 통하여 복약 정보 추출
        String analysis=aiService.analysis(parseData);
        log.info("gemini 요청 완료");

        AiResponseDto aiResponseDto=aiService.parseGeminiResponse(analysis);
        log.info("추출 데이터 파싱 완료, api token 비용: {}", aiResponseDto.getTotalTokenCount());

        List<RoutinePrescriptionResponse> response=aiResponseDto.getDoseDtos().stream().map(doseDto -> {
            log.info("분석한 의약품 정보 체크: {}", doseDto.toString());
            MedicineDocument medicineDocument=medicineDocumentService.findMedicineByEdiCodeAndItemName(doseDto.getEdiCode(), doseDto.getName(), 1)
                    .getFirst();

            return RoutinePrescriptionResponse.builder()
                    .medicineId(medicineDocument.getId())
                    .imageUrl(medicineDocument.getItemImage())
                    .medicineName(medicineDocument.getItemName())
                    .dose(doseDto.getDose())
                    .totalQuantity(doseDto.getTotalDays()*doseDto.getDose()*doseDto.getScheduleCount())
                    .userSchedules(userSchedules)
                    .dayOfWeeks(List.of(1,2,3,4,5,6,7))
                    .build()
                    ;
        }).toList();

        return response;
    }

    /**
     * 약을 먹는 시기 구하는 메서드
     * 사용자가 하루에 약을 먹는 횟수에 따라서 아침, 점심, 저녁, 자기전 중 언제 먹을지 설정
     *
     * TODO 루틴 등록시에는 약을 먹는 시기를 명시하지만, 처방전 등록시에는 자동으로 설정하던, 미리 설정 값을 입력받는 쪽으로 구현
     * */
    private List<String> convertTypeCountToTypes(int typeCount) {

        if(typeCount==1){
            return List.of("LUNCH");
        }

        if(typeCount==2){
            return List.of("MORNING", "DINNER");
        }

        if(typeCount==3){
            return List.of("MORNING", "LUNCH", "DINNER");
        }

        if(typeCount==4){
            return List.of("MORNING", "LUNCH", "DINNER", "BEDTIME");
        }

        throw new ApiException(ErrorCode.BAD_REQEUST, "잘못된 type count 입력");
    }

    /**
     * 루틴 제거 메서드
     * */
    public void deleteRoutine(Long userId, Long routineMedicineId) {
        // routine 존재 여부 파악
        routineMedicineService.deleteRoutine(routineMedicineId);
    }

    public String convertToJson(String clientId, String medicineName, LocalDateTime dateTime) {
        Map<String, Object> alarmData= new HashMap<>();
        alarmData.put("client_id", clientId);
        alarmData.put("medicine_name", medicineName);
        alarmData.put("date_time", dateTime.toString());

        try {
            return objectMapper.writeValueAsString(alarmData);
        }catch (Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR, "rouitne json 변환 중 오류");
        }
    }


}
