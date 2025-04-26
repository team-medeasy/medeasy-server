package com.medeasy.domain.interested_medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.interested_medicine.db.InterestedMedicineEntity;
import com.medeasy.domain.interested_medicine.dto.InterestedMedicineRegisterRequest;
import com.medeasy.domain.interested_medicine.service.InterestedMedicineService;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class InterestedMedicineBusiness {
    private final InterestedMedicineService interestedMedicineService;
    private final UserService userService;
    private final MedicineDocumentService medicineDocumentService;

    public void registerInterestedMedicine(Long userId, InterestedMedicineRegisterRequest request) {
        UserEntity userEntity=userService.getUserById(userId);
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(request.getMedicineId());

        InterestedMedicineEntity entity=InterestedMedicineEntity.builder()
                        .medicineId(request.getMedicineId())
                        .user(userEntity)
                        .build()
                        ;

        interestedMedicineService.saveInterestedMedicine(entity);
    }

    public void getInterestedMedicines(Long userId, int page, int size) {

    }
}
