package com.medeasy.domain.routine.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.ai.dto.AiResponseDto;
import com.medeasy.domain.ai.service.GeminiPrescriptionAiService;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.ocr.dto.OcrParsedDto;
import com.medeasy.domain.ocr.service.OcrServiceByMultipart;
import com.medeasy.domain.routine.converter.RoutineConverter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineQueryRepository;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.*;
import com.medeasy.domain.routine.event.RoutineEventService;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.routine_group.converter.RoutineGroupConverter;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupRepository;
import com.medeasy.domain.routine_group.service.RoutineDateRangeStrategy;
import com.medeasy.domain.routine_group.service.RoutineGroupService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_schedule.business.UserScheduleBusiness;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.MedicationTime;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Business
public class RoutineBusiness {
    private final ObjectMapper objectMapper;

    private final UserService userService;
    private final RoutineService routineService;
    private final RoutineGroupService routineGroupService;
    private final MedicineDocumentService medicineDocumentService;

    private final RoutineGroupConverter routineGroupConverter;

    private final OcrServiceByMultipart ocrService;
    private final GeminiPrescriptionAiService geminiPrescriptionAiService;

    private final UserScheduleConverter userScheduleConverter;
    private final RoutineRepository routineRepository;
    private final RoutineGroupRepository routineGroupRepository;

    private final RoutineQueryRepository routineQueryRepository;
    private final RoutineConverter routineConverter;
    private final UserScheduleService userScheduleService;

    private final RoutineCalculator routineCalculatorByDayOfWeeks;
    private final RoutineCalculator routineCalculatorByInterval;

    private final RoutineCreator routineBasicCreator;
    private final RoutineCreator routineContainPastCreator;
    private final RoutineCreator routineFutureCreator;
    private final UserScheduleBusiness userScheduleBusiness;
    private final RoutineEventService routineEventService;

    // 생성자 주입 + @Qualifier 적용
    public RoutineBusiness(
            RoutineService routineService,
            RoutineGroupService routineGroupService,
            UserService userService,
            MedicineDocumentService medicineDocumentService,

            OcrServiceByMultipart ocrService,
            GeminiPrescriptionAiService geminiPrescriptionAiService,

            ObjectMapper objectMapper,

            UserScheduleConverter userScheduleConverter,
            RoutineGroupConverter routineGroupConverter,

            RoutineRepository routineRepository,
            RoutineQueryRepository routineQueryRepository,
            RoutineGroupRepository routineGroupRepository,
            RoutineConverter routineConverter, UserScheduleService userScheduleService,
            @Qualifier("routineCalculatorByDayOfWeeks") RoutineCalculator routineCalculatorByDayOfWeeks,
            @Qualifier("routineCalculatorByInterval") RoutineCalculator routineCalculatorByInterval,
            @Qualifier("routineBasicCreator") RoutineCreator routineBasicCreator,
            @Qualifier("routineContainPastCreator") RoutineCreator routineContainPastCreator,
            @Qualifier("routineFutureCreator") RoutineCreator routineFutureCreator,
            UserScheduleBusiness userScheduleBusiness, RoutineEventService routineEventService) {
        this.routineService = routineService;
        this.routineGroupService = routineGroupService;
        this.userService = userService;
        this.medicineDocumentService = medicineDocumentService;
        this.ocrService = ocrService;
        this.geminiPrescriptionAiService = geminiPrescriptionAiService;

        this.userScheduleBusiness = userScheduleBusiness;
        this.objectMapper = objectMapper;

        this.userScheduleConverter = userScheduleConverter;
        this.routineGroupConverter = routineGroupConverter;

        this.routineRepository = routineRepository;
        this.routineQueryRepository = routineQueryRepository;
        this.routineGroupRepository = routineGroupRepository;

        this.routineConverter = routineConverter;
        this.userScheduleService = userScheduleService;

        this.routineCalculatorByDayOfWeeks = routineCalculatorByDayOfWeeks;
        this.routineCalculatorByInterval = routineCalculatorByInterval;

        this.routineBasicCreator = routineBasicCreator;
        this.routineContainPastCreator = routineContainPastCreator;
        this.routineFutureCreator = routineFutureCreator;
        this.routineEventService = routineEventService;
    }
    /**
     * 단일 약 루틴 저장
     *
     * 3/16 업데이트
     *
     *
     * */
    @Transactional
    public void registerRoutine(Long userId, RoutineRegisterRequest routineRegisterRequest) {
        // Entity 값 가져오기 user_schedule.time 은 오름차순
        UserEntity userEntity = userService.getUserByIdToFetchJoin(userId);
        List<UserScheduleEntity> userScheduleEntities=userEntity.getUserSchedules();

        // request 에 포함된 schedule 정보 가져오기
        List<UserScheduleEntity> registerUserScheduleEntities = userScheduleEntities.stream()
                .filter(userScheduleEntity -> routineRegisterRequest.getUserScheduleIds().contains(userScheduleEntity.getId()))
                .toList();

        // 요청에 들어간 user_schedule_id가 존재하지 않을 경우 예외 발생
        if(registerUserScheduleEntities.size() != routineRegisterRequest.getUserScheduleIds().size()){
            throw new ApiException(SchedulerError.NOT_FOUND);
        }

        List<RoutineEntity> routineEntities = new ArrayList<>();

        RoutineCalculator routineCalculator;

        // TODO 루틴 등록 프론트에서 간격으로 적용 완료시 삭제 필요 버전 호환성 코드
        if(routineRegisterRequest.getIntervalDays() == null){
            routineCalculator=routineCalculatorByDayOfWeeks;
        }else {
            routineCalculator=routineCalculatorByInterval;
        }

        // 전략 패턴 이용 상황에 따른 루틴 등록
        if(routineRegisterRequest.getRoutineStartDate() == null && routineRegisterRequest.getStartUserScheduleId() == null) {
            // 루틴에 시작날짜, 시간 명시하지 않은 경우 (제일 기본)
            routineEntities = routineBasicCreator.createRoutines(routineCalculator, routineRegisterRequest, userEntity, registerUserScheduleEntities);

        } else if (routineRegisterRequest.getRoutineStartDate().isBefore(LocalDate.now()) || routineRegisterRequest.getRoutineStartDate().isEqual(LocalDate.now())) {
            // 루틴에 과거 일자가 포함되어있는 경우
            routineEntities = routineContainPastCreator.createRoutines(routineCalculator, routineRegisterRequest, userEntity, registerUserScheduleEntities);

        } else if (routineRegisterRequest.getRoutineStartDate().isAfter(LocalDate.now())) {
            // 루틴을 미리 등록할 경우
            routineEntities = routineFutureCreator.createRoutines(routineCalculator, routineRegisterRequest, userEntity, registerUserScheduleEntities);
        }

        RoutineGroupEntity routineGroupEntity = routineGroupConverter.toEntityByRequest(routineRegisterRequest);
        routineGroupEntity.setUser(userEntity);
        routineGroupService.mappingRoutineGroup(routineGroupEntity, routineEntities);
        routineRepository.saveAll(routineEntities);
    }


    @Transactional
    public void registerRoutineList(Long userId, List<RoutineRegisterRequest> routinesRegisterRequest) {
        routinesRegisterRequest.forEach(routineRegisterRequest -> {
            registerRoutine(userId, routineRegisterRequest); // 자기 자신을 호출하게되면 프록시 객체를 거치지 않기 때문에 트랜잭션 적용 x
        });
    }


    public List<LocalDate> calculateRoutineDatesByTotalDays(RoutineRegisterRequestByTotalDays routineRegisterRequest) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        int requiredDays = routineRegisterRequest.getTotalDays();

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
     *
     * 체크한 루틴과 같은 시간대의 다른 루틴들도 전부 체크되어있을 경우에 알림 전송
     *
     * */
    @Transactional
    public RoutineCheckResponse checkRoutine(Long userId, Long routineId, Boolean isTaken) {
        List<RoutineEntity> routineEntities=routineService.getUserRoutinesInSameTimes(userId, routineId);

        RoutineEntity targetRoutine = null;
        Boolean beforeTaken = null;

        for (RoutineEntity routine : routineEntities) {
            if (routine.getId().equals(routineId)) {
                targetRoutine = routine;
                beforeTaken = routine.getIsTaken();
                routine.setIsTaken(isTaken);
                break;
            }
        }

        if (targetRoutine == null) {
            throw new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE, "요청한 루틴을 찾을 수 없습니다: " + routineId);
        }

        boolean allTaken = true;
        for (RoutineEntity routine : routineEntities) {
            if (!routine.getIsTaken()) {
                allTaken = false;
                break;
            }
        }

        if (allTaken) {
            routineEventService.publishRoutineCheckEvent(userId, targetRoutine.getUserSchedule().getName());
        }

        return RoutineCheckResponse.builder()
                .routineId(routineId)
                .afterIsTaken(isTaken)
                .beforeIsTaken(beforeTaken)
                .build()
                ;
    }

    @Transactional
    public List<RoutineCheckResponse> checkScheduleRoutines(Long userId, Long scheduleId, LocalDate startDate, LocalDate endDate) {
        List<RoutineEntity> routineEntities=routineService.getRoutinesOnScheduleIdAndTakeDate(userId, scheduleId, startDate, endDate);

        List<RoutineCheckResponse> responses = routineEntities.stream()
                .map(routineEntity -> {
                    Boolean beforeTaken = routineEntity.getIsTaken();
                    routineEntity.setIsTaken(true);

                    return RoutineCheckResponse.builder()
                            .routineId(routineEntity.getId())
                            .beforeIsTaken(beforeTaken)
                            .afterIsTaken(true)
                            .build();
                })
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * 처방전 루틴 등록 메서드
     *
     * 처방전 분석 데이터를 토대로 루틴 추가 여부 응답을 전송한다.
     * */
    public List<RoutinePrescriptionResponse> registerRoutineByPrescription(Long userId, MultipartFile file) {
        var userEntity = userService.getUserByIdToFetchJoin(userId);
        List<UserScheduleDto> userScheduleDtos=userEntity.getUserSchedules().stream().map(userScheduleConverter::toDto).toList();

        Map<String, UserScheduleDto> scheduleMap = userScheduleDtos
                .stream()
                .collect(Collectors.toMap(
                        UserScheduleDto::getName,
                        Function.identity()
                ));

        // 처방전 이미지 파싱
        List<OcrParsedDto> parseData=ocrService.sendOcrRequest(file);
        log.info("처방전 이미지 ocr 분석 완료");

        // ai api를 통하여 복약 정보 추출
        String analysis= geminiPrescriptionAiService.analysis(parseData);
        log.info("gemini 요청 완료");

        // TODO aiResponseDTO에서 나온 schedule_count에 따라서
        AiResponseDto aiResponseDto= geminiPrescriptionAiService.parseGeminiResponse(analysis);
        log.info("추출 데이터 파싱 완료, api token 비용: {}", aiResponseDto.getTotalTokenCount());

        List<RoutinePrescriptionResponse> response=aiResponseDto.getDoseDtos().stream().map(doseDto -> {
            log.info("분석한 의약품 정보 체크: {}", doseDto.toString());
            MedicineDocument medicineDocument=medicineDocumentService.findMedicineByEdiCodeAndItemName(doseDto.getEdiCode(), doseDto.getName(), 1)
                    .getFirst();

            List<UserScheduleDto> recommendedUserScheduleDtos=recommendScheduleByScheduleCount(scheduleMap, doseDto.getScheduleCount());


            return RoutinePrescriptionResponse.builder()
                    .medicineId(medicineDocument.getId())
                    .imageUrl(medicineDocument.getItemImage())
                    .medicineName(medicineDocument.getItemName())
                    .dose(doseDto.getDose())
                    .totalQuantity(doseDto.getTotalDays()*doseDto.getDose()*doseDto.getScheduleCount())
                    .userSchedules(recommendedUserScheduleDtos)
                    .dayOfWeeks(List.of(1,2,3,4,5,6,7))
                    .totalDays(doseDto.getTotalDays())
                    .entpName(medicineDocument.getEntpName())
                    .className(medicineDocument.getClassName())
                    .etcOtcName(medicineDocument.getEtcOtcName())
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
    private List<UserScheduleDto> recommendScheduleByScheduleCount(Map<String, UserScheduleDto> scheduleMap, int scheduleCount) {
        // 하나의 약마다 개별적인 스케줄 제공을 위한 map 복사
        Map<String, UserScheduleDto> copiedMap = scheduleMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> UserScheduleDto.builder()
                                .userScheduleId(entry.getValue().getUserScheduleId())
                                .name(entry.getValue().getName())
                                .takeTime(entry.getValue().getTakeTime())
                                .isRecommended(false) // 기본 false로 설정
                                .build()
                ));

        List<MedicationTime> medicationTimes;

        if(scheduleCount==1){
            medicationTimes = List.of(MedicationTime.LUNCH);
        } else if (scheduleCount==2){
            medicationTimes = List.of(MedicationTime.MORNING, MedicationTime.DINNER);
        } else if (scheduleCount==3) {
            medicationTimes = List.of(MedicationTime.MORNING, MedicationTime.LUNCH, MedicationTime.DINNER);
        } else if (scheduleCount==4){
            medicationTimes = List.of(MedicationTime.MORNING, MedicationTime.LUNCH, MedicationTime.DINNER, MedicationTime.BED_TIME);
        } else {
            throw new ApiException(SchedulerError.BAD_REQEUST, "지원하는 스케줄 외 요청");
        }

        return userScheduleConverter.toDtoListFromMedicationTimes(copiedMap, medicationTimes);
    }

    /**
     * 루틴 제거 메서드
     * */
    @Transactional
    public void deleteRoutine(Long userId, Long routineId) {
        // routine 존재 여부 파악
        routineService.deleteRoutineByUserIdAndId(userId, routineId);
    }

    @Deprecated
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

    /**
     * 현재 또는 과거 복용하였던 의약품과 routine_group정보를 같이 조회하는 메서드
     *
     * 현재 복용 중인 약, 과거 복용하였던 약 페이지에서 사용
     * strategy 패턴을 통해 구현 -> 현재 복용 약, 과거 복용 약 범위 설정 가능
     * 과거 복용 약의 경우 startDate, endDate 추가하여 조회 범위 설정 가능
     *
     * interval days의 경우 루틴 업데이트 날짜 이후로 조회
     * */
    @Transactional
    public List<CurrentRoutineMedicineResponse> getRoutineList(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            RoutineDateRangeStrategy routineDateRangeStrategy
    ) {
        // 현재 복용 중인 약 복용 기한 가져오기 (routine_group_id, start_date, end_date)
        List<RoutineGroupDateRangeDto> routineGroupDateRangeDtos=routineDateRangeStrategy.findRoutineGroupDateRanges(userId, startDate, endDate);
        Map<Long, RoutineGroupDateRangeDto> mapGroupIdToDateRange =
                routineGroupDateRangeDtos.stream()
                        .collect(Collectors.toMap(
                                RoutineGroupDateRangeDto::getRoutineGroupId,
                                Function.identity()
                        ));

        // routine group list
        List<Long> routineGroupIds = routineGroupDateRangeDtos.stream().map(RoutineGroupDateRangeDto::getRoutineGroupId).toList();

        // batch query 루틴 정보 조회
        List<RoutineFlatDto> routineFlatDtos = routineQueryRepository.findRoutineInfoByUserIdAndGroupIds(userId, routineGroupIds);

        // routine list, routine_group과 매칭
        Map<Long, List<RoutineFlatDto>> mapGroupedByGroupId = routineFlatDtos.stream()
                .collect(Collectors.groupingBy(RoutineFlatDto::getRoutineGroupId));

        /**
         * 두개의 List Fetch 로인해 MultipleBagExecption이 발생
         * 따라서 Native Query를 통해 필요한 정보만 가져올 예정
         *
         * user_schedule(id), routine_medicine(medicine_id, nickname, dose), routine(take_date)
         * */

        // 응답 생성
        return routineGroupIds.stream().map(routineGroupId->{
            RoutineGroupDateRangeDto routineGroupDateRangeDto=mapGroupIdToDateRange.get(routineGroupId);
            List<RoutineFlatDto> routines=mapGroupedByGroupId.get(routineGroupId);

            List<Long> routineIds = routines.stream().map(RoutineFlatDto::getRoutineId).toList();
            List<LocalDate> takeDates=routines.stream().map(RoutineFlatDto::getTakeDate).toList();
            LocalDate updatedDate=routineGroupDateRangeDto.getUpdatedAt().toLocalDate();

            List<Long> userScheduleIds=routines.stream().map(RoutineFlatDto::getUserScheduleId).distinct().toList();

            RoutineFlatDto routineFlat= routines.getFirst();

            // 약 정보 가져오기
            String medicineId = routineFlat.getMedicineId();
            MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(medicineId);

            Integer intervalDays = calculateIntervalDaysAfterUpdatedAt(takeDates, updatedDate);

            return CurrentRoutineMedicineResponse.builder()
                    .routineGroupId(routineGroupId)
                    .routineIds(routineIds)
                    .medicineId(medicineDocument.getId())
                    .intervalDays(intervalDays)
                    .nickname(routineFlat.getNickname())
                    .medicineName(medicineDocument.getItemName())
                    .medicineImage(medicineDocument.getItemImage())
                    .entpName(medicineDocument.getEntpName())
                    .etcOtcName(medicineDocument.getEtcOtcName())
                    .className(medicineDocument.getClassName())
                    .dose(routineFlat.getDose())
                    .scheduleSize(userScheduleIds.size())
                    .routineStartDate(routineGroupDateRangeDto.getStartDate())
                    .routineEndDate(routineGroupDateRangeDto.getEndDate())
                    .intervalDays(intervalDays)
                    .itemSeq(medicineDocument.getItemSeq())
                    .build()
                    ;
        }).toList();
    }

    /**
     * 루틴 업데이트 메서드
     * 일단 routine_medicine_id가 포함된 routine_group까지 올라가서 값들을 가져오고
     *
     * user_schedule, routine, routine_medicine, routine_group
     *
     * 이미 복용한 루틴은 패스, 나머지 약들로 재배치
     *
     * 1. routine_medicine_id가 포함된 routine_group_id 추출 -> 불가 routine은 여러 group_id에 속해있음.
     * 2. routine_group_id에 속해있는 routine_medicine list 조회 -> is_taken false
     * 3. routine_medicine false list 전부 delete
     * 4. 투여일수에 현재 복용한 일수를 제외한 일수에 대해서 수정 요청 데이터를 반영하여 routine_medicine 저장
     *
     * */
    @Transactional
    public void putRoutineGroup(Long userId, RoutineUpdateRequest routineUpdateRequest) {
        // 사용자와 스케줄 관련 처리
        UserEntity userEntity = userService.getUserByIdToFetchJoin(userId);
        List<UserScheduleEntity> userScheduleEntities = userEntity.getUserSchedules();

        // 루틴 그룹 조회
        RoutineGroupEntity routineGroupEntity=routineGroupService.findRoutineGroupContainsRoutineIdByUserId(userId, routineUpdateRequest.getRoutineId());
        List<RoutineEntity> routineEntities=routineGroupEntity.getRoutines();

        // 과거 스케줄 리스트 조회
        List<Long> pastUserScheduleIds = userScheduleBusiness.getDistinctUserScheduleIds(routineEntities, routineGroupEntity.getUpdatedAt().toLocalDate(), routineGroupEntity.getUpdatedAt().toLocalTime());

        List<Long> userScheduleIds = routineUpdateRequest.getUserScheduleIds() != null ? routineUpdateRequest.getUserScheduleIds() : pastUserScheduleIds;
        List<UserScheduleEntity> registerUserScheduleEntities = userScheduleBusiness.validationRequest(userScheduleEntities, userScheduleIds);

        List<LocalDate> sortedTakeDates = routineEntities.stream()
                .map(RoutineEntity::getTakeDate)
                .distinct()
                .sorted()
                .toList();

        int pastIntervalDays = calculateIntervalDays(sortedTakeDates);


        // 복용한 일정과 하지 않은 엔티티 분리
        List<RoutineEntity> takenRoutines = routineEntities.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsTaken()))
                .toList();

        List<RoutineEntity> notTakenRoutines = routineEntities.stream()
                .filter(r -> Boolean.FALSE.equals(r.getIsTaken()))
                .toList();

        int remainingDoseTotal = (int) (routineGroupEntity.getDose() * notTakenRoutines.size());

        String medicineId = routineUpdateRequest.getMedicineId() != null ? routineUpdateRequest.getMedicineId() : routineGroupEntity.getMedicineId();
        String nickname = routineUpdateRequest.getNickname() != null ? routineUpdateRequest.getNickname() : routineGroupEntity.getNickname();
        int dose = routineUpdateRequest.getDose() != null ? routineUpdateRequest.getDose() : routineGroupEntity.getDose();
        int totalQuantity = routineUpdateRequest.getTotalQuantity() != null ? routineUpdateRequest.getTotalQuantity() : remainingDoseTotal;
        int intervalDays = routineUpdateRequest.getIntervalDays() != null ? routineUpdateRequest.getIntervalDays() : pastIntervalDays;


        /**
         * 새로 등록할 루틴의 시작 날짜와 시작 스케줄
         *
         * 마지막날 먹은 약의 개수 비교
         * last Taken >= request 다음날 부터 루틴 생성
         * last Taken < request 마지막날 루틴 추가 생성
         * */
        RoutineEntity lastTakenRoutine=getLastTakenRoutine(takenRoutines, notTakenRoutines);
        LocalDate lastTakenDate = lastTakenRoutine.getTakeDate();

        // 마지막 날 루틴 조회
        List<RoutineEntity> routinesOnLastTakenDate = takenRoutines.stream()
                .filter(r -> lastTakenDate.equals(r.getTakeDate()))
                .toList();

        List<Long> updateUserScheduleIds = routineUpdateRequest.getUserScheduleIds() != null ? routineUpdateRequest.getUserScheduleIds() : pastUserScheduleIds;
        List<Long> sortedUserScheduleIds=userScheduleBusiness.sortUserScheduleIdsByTakeTimeAsc(updateUserScheduleIds);

        int lastTakenDateDose=routineGroupEntity.getDose()*routinesOnLastTakenDate.size();
        int requestDateDose=sortedUserScheduleIds.size()*dose;

        LocalDate startDate;
        Long startUserScheduleId;

        // 시작 날짜 계산
        if(lastTakenDateDose >= requestDateDose){ // 마지막 날 복용량을 다 채운 경우
            startDate=lastTakenDate.plusDays(intervalDays);
            startUserScheduleId=sortedUserScheduleIds.getFirst();
        }else{
            startDate=lastTakenDate;
            int plusSchedule=(requestDateDose-lastTakenDateDose)/dose;

            // dose=1인 경우에 lastTakenDateDose = 2  requestDateDose = 3 -> 스케줄 하나 추가 -> 리스트 중에서 마지막 요소 즉 2
            startUserScheduleId=registerUserScheduleEntities.get(registerUserScheduleEntities.size()-plusSchedule).getId();
        }

        RoutineRegisterRequest routineRegisterRequest=new RoutineRegisterRequest(medicineId, nickname, dose, totalQuantity, null, userScheduleIds, startDate, startUserScheduleId, intervalDays);
        List<RoutineEntity> newRoutineEntities=routineFutureCreator.createRoutines(routineCalculatorByInterval, routineRegisterRequest, userEntity, registerUserScheduleEntities);


        // 복용하지 않은 루틴 삭제
        routineGroupEntity.getRoutines().removeAll(notTakenRoutines);

        // 새 루틴 저장 및 매핑
        routineGroupEntity.mappingWithRoutines(newRoutineEntities);
        routineGroupEntity.updateRoutine(nickname, medicineId, dose);

        routineService.saveAll(newRoutineEntities);
//        routineService.deleteRoutines(notTakenRoutines); // deleteAll 쿼리를 날렸지만, routine_group 컬렉션에는 여전히 남아있기 때문에 지워지지 않음
    }

    @Transactional
    public void deleteGroupRoutine(Long userId, Long routineId) {
        RoutineEntity routineEntity=routineService.getUserRoutineById(userId, routineId);
        RoutineGroupEntity routineGroupEntity=routineEntity.getRoutineGroup();
        routineGroupRepository.delete(routineGroupEntity);
    }

    @Transactional
    public void deleteRoutineGroup(Long userId, Long routineGroupId) {
        RoutineGroupEntity routineGroupEntity=routineGroupService.findRoutineGroupById(routineGroupId);
        routineGroupRepository.delete(routineGroupEntity);
    }

    @Transactional
    public void patchRoutineNickname(Long userId, Long routineId, String newNickname) {
        RoutineGroupEntity routineGroupEntity = routineGroupService.findByRoutineIdAndUserId(routineId, userId);
        routineGroupEntity.setNickname(newNickname);
    }

    /**
     * 날짜 리스트를 입력받아 중복되지 않은 날짜들에 대한 간격 구하는 메서드
     * */
    public int calculateIntervalDays(List<LocalDate> dates) {
        List<LocalDate> sortedDates=dates.stream().distinct().sorted(Comparator.naturalOrder()).toList();
        int intervalDays = 1;
        if(sortedDates.size() != 1 ){
            intervalDays = (int) ChronoUnit.DAYS.between(
                    sortedDates.get(0),
                    sortedDates.get(1)
            );
        }
        return intervalDays;
    }

    /**
     * 날짜 리스트를 입력받아 중복되지 않은 날짜들에 대한 간격 구하는 메서드
     * */
    public int calculateIntervalDaysAfterUpdatedAt(List<LocalDate> dates, LocalDate updatedAt) {
        List<LocalDate> sortedDates=dates.stream().distinct().sorted(Comparator.naturalOrder()).toList();

        int intervalDays = 1;

        if(sortedDates.size() != 1 ){
            intervalDays = (int) ChronoUnit.DAYS.between(
                    sortedDates.get(sortedDates.size()-2),
                    sortedDates.getLast()
            );
        }
        return intervalDays;
    }

    public int calculateRemainQuantity(List<RoutineEntity> routineEntities, int dose){
        // 복용한 일정과 하지 않은 엔티티 분리
        List<RoutineEntity> takenRoutines = routineEntities.stream()
                .filter(r -> Boolean.TRUE.equals(r.getIsTaken()))
                .toList();

        List<RoutineEntity> notTakenRoutines = routineEntities.stream()
                .filter(r -> Boolean.FALSE.equals(r.getIsTaken()))
                .toList();

        int remainingDoseTotal = (int) (dose * notTakenRoutines.size());
        return remainingDoseTotal;
    }

    /**
     * 복용한 루틴 리스트 중 가장 마지막 루틴 반환
     *
     * 복용한 루틴이 없다면 복용하지 않은 것 중 첫번째 요소 반환
     * */
    public RoutineEntity getLastTakenRoutine(List<RoutineEntity> takenRoutines, List<RoutineEntity> notTakenRoutines) {
        if(takenRoutines.isEmpty()){
            return notTakenRoutines.getFirst();
        }

        return takenRoutines.getLast();
    }

    @Transactional
    public RoutineGroupInfoResponse getRoutineGroupInfo(Long userId, Long routineId) {
        UserEntity userEntity = userService.getUserByIdToFetchJoin(userId);
        List<UserScheduleEntity> userScheduleEntities = userEntity.getUserSchedules();

        RoutineGroupEntity routineGroupEntity=routineGroupService.findRoutineGroupContainsRoutineIdByUserId(userId, routineId);
        List<RoutineEntity> routineEntities = routineGroupEntity.getRoutines();
        List<Long> routineIds = routineEntities.stream().map(RoutineEntity::getId).toList();

        List<LocalDate> takeDates = routineEntities.stream().map(RoutineEntity::getTakeDate).distinct().sorted().toList();

        int intervalDays = calculateIntervalDays(takeDates);
        int remainQuantity=calculateRemainQuantity(routineEntities, routineGroupEntity.getDose());
        List<Long> routineGroupUserScheduleIds=userScheduleBusiness.getDistinctUserScheduleIds(routineEntities, routineGroupEntity.getUpdatedAt().toLocalDate(), routineGroupEntity.getUpdatedAt().toLocalTime());

        List<RoutineGroupInfoResponse.ScheduleResponse> scheduleResponses=userScheduleEntities.stream().map(userScheduleEntity -> {
                    boolean isSelected = false;

                    if(routineGroupUserScheduleIds.contains(userScheduleEntity.getId())){
                        isSelected=true;
                    }

                    return RoutineGroupInfoResponse.ScheduleResponse.builder()
                            .name(userScheduleEntity.getName())
                            .userScheduleId(userScheduleEntity.getId())
                            .takeTime(userScheduleEntity.getTakeTime())
                            .isSelected(isSelected)
                            .build()
                            ;
        }).toList();

        return RoutineGroupInfoResponse.builder()
                .routineGroupId(routineGroupEntity.getId())
                .routineIds(routineIds)
                .nickname(routineGroupEntity.getNickname())
                .dose(routineGroupEntity.getDose())
                .medicineId(routineGroupEntity.getMedicineId())
                .intervalDays(intervalDays)
                .remainingQuantity(remainQuantity)
                .scheduleResponses(scheduleResponses)
                .build()
                ;
    }

    public Long getRoutineId(Long userId, String medicineId) {
        return routineGroupService.findUserRoutineGroupByMedicineId(userId, medicineId);
    }

    /**
     * 복용하지 않은 루틴 중, 제시한 의약품 이름 또는 닉네임과 유사한 루틴 복용 여부 체크
     * */
    @Transactional
    public RoutineCheckResponse checkRoutineByMedicineName(Long userId, String medicineName, Long scheduleId) {
        List<RoutineEntity> routineEntities=routineService.getNotTakenRoutinesOnScheduleIdWithRoutineGroup(userId, scheduleId);
        List<RoutineGroupEntity> routineGroupEntities=routineEntities.stream().map(RoutineEntity::getRoutineGroup).toList();

        // 약 이름과 가장 유사한 루틴 그룹 찾기 (유사도 기준 내림차순 정렬)
        RoutineGroupEntity targetGroup = routineGroupEntities.stream()
                .map(group -> Map.entry(group, calculateSimilarity(group.getNickname(), medicineName)))
                .filter(entry -> entry.getValue() > 0.4)  // 유사도 70% 이상만 고려
                .sorted(Map.Entry.<RoutineGroupEntity, Double>comparingByValue().reversed())  // 유사도 내림차순 정렬
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQEUST, "말씀하신 약 이름에 해당하는 일정을 못찾았습니다. 조금 더 자세히 말씀해 주세요. " + medicineName));

        RoutineEntity targetRoutine = routineEntities.stream()
                .filter(routine -> routine.getRoutineGroup().getId().equals(targetGroup.getId()))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR, "해당 스케줄의 루틴을 찾을 수 없습니다."));

        targetRoutine.setIsTaken(true);

        return RoutineCheckResponse.builder()
                .routineId(targetRoutine.getId())
                .beforeIsTaken(false) // 복용하지 않은 루틴들만 가져오기 때문에 무조건 false
                .afterIsTaken(true)
                .build()
                ;
    }

    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        // 괄호와 내용 제거 (옵션)
        String preprocessed1 = s1.replaceAll("\\([^\\)]*\\)", "");
        String preprocessed2 = s2.replaceAll("\\([^\\)]*\\)", "");

        String str1 = preprocessed1.toLowerCase().trim();
        String str2 = preprocessed2.toLowerCase().trim();

        int distance = new LevenshteinDistance().apply(str1, str2);
        return 1.0 - (double) distance / Math.max(str1.length(), str2.length());
    }
}
