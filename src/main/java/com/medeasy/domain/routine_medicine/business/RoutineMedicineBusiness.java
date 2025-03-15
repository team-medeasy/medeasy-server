package com.medeasy.domain.routine_medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.routine_medicine.service.RoutineMedicineService;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class RoutineMedicineBusiness {

    private final RoutineMedicineService routineMedicineService;
}
