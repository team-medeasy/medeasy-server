package com.medeasy.domain.routine_medicine.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineMedicineService {

    private final RoutineMedicineRepository routineMedicineRepository;

    public void saveAll(List<RoutineMedicineEntity> routineMedicineEntities) {
        routineMedicineRepository.saveAll(routineMedicineEntities);
    }

    public RoutineMedicineEntity findById(Long id) {
        return routineMedicineRepository.findById(id).orElseThrow(()-> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    public RoutineMedicineEntity save(RoutineMedicineEntity routineMedicineEntity) {
        return routineMedicineRepository.save(routineMedicineEntity);
    }

    @Transactional
    public void deleteRoutine(Long routineId) {
        try {
            routineMedicineRepository.deleteById(routineId);
        } catch (EmptyResultDataAccessException e) {
            throw new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE);
        } catch (DataAccessException e) {
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
    }
}
