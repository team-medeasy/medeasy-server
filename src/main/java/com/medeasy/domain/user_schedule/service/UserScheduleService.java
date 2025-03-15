package com.medeasy.domain.user_schedule.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserScheduleService {

    private final UserScheduleRepository userScheduleRepository;

    public UserScheduleEntity save(UserScheduleEntity userScheduleEntity) {
        try {
            return userScheduleRepository.save(userScheduleEntity);
        }catch (Exception e) {
            throw new ApiException(ErrorCode.SERVER_ERROR, "사용자 스케줄 등록 중 오류 발생");
        }
    }

    public List<UserScheduleEntity> saveAll(List<UserScheduleEntity> userScheduleEntities) {
        return userScheduleRepository.saveAll(userScheduleEntities);
    }

    public UserScheduleEntity findById(Long id) {
        return userScheduleRepository.findById(id).orElseThrow(()->new ApiException(ErrorCode.BAD_REQEUST, "사용자 스케줄을 찾을 수 없습니다."));
    }

    public List<UserScheduleEntity> findAllByIdInOrderByTakeTimeAsc(List<Long> ids) {
        return userScheduleRepository.findAllByIdInOrderByTakeTimeAsc(ids);
    }
}
