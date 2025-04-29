package com.medeasy.domain.interested_medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.interested_medicine.db.InterestedMedicineEntity;
import com.medeasy.domain.interested_medicine.dto.InterestedMedicineRegisterRequest;
import com.medeasy.domain.interested_medicine.dto.InterestedMedicineResponse;
import com.medeasy.domain.interested_medicine.service.InterestedMedicineService;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Business
@RequiredArgsConstructor
public class InterestedMedicineBusiness {
    private final InterestedMedicineService interestedMedicineService;
    private final UserService userService;
    private final MedicineDocumentService medicineDocumentService;

    @Transactional
    public void registerInterestedMedicine(Long userId, InterestedMedicineRegisterRequest request) {
        UserEntity userEntity=userService.getUserById(userId);
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(request.getMedicineId());

        Optional<InterestedMedicineEntity> interestedMedicineEntityOptional=interestedMedicineService.getOptionalInterestedMedicine(userId, medicineDocument.getId());

        if(interestedMedicineEntityOptional.isPresent()){
            interestedMedicineService.deleteInterestedMedicine(interestedMedicineEntityOptional.get());
        }else {
            InterestedMedicineEntity entity=InterestedMedicineEntity.builder()
                    .medicineId(request.getMedicineId())
                    .user(userEntity)
                    .build()
                    ;

            interestedMedicineService.saveInterestedMedicine(entity);
        }
    }

    public List<InterestedMedicineResponse> getInterestedMedicines(Long userId, int page, int size) {
        List<InterestedMedicineEntity> interestedMedicineEntityList=interestedMedicineService.getInterestedMedicinePageable(userId, page, size);

        return interestedMedicineEntityList.stream().map(entity -> {
            MedicineDocument medicine=medicineDocumentService.findMedicineDocumentById(entity.getMedicineId());
            return InterestedMedicineResponse.builder()
                    .interestedMedicineId(entity.getId())
                    .itemName(medicine.getItemName())
                    .itemImage(medicine.getItemImage())
                    .entpName(medicine.getEntpName())
                    .className(medicine.getClassName())
                    .etcOtcName(medicine.getEtcOtcName())
                    .medicineId(medicine.getId())
                    .build()
                    ;
        }).toList();
    }
}
