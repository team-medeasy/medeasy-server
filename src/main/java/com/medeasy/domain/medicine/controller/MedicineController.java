package com.medeasy.domain.medicine.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.medicine.business.MedicineBusiness;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineBusiness medicineBusiness;

    @GetMapping("")
    public Api<Page<MedicineResponse>> getMedicine(Pageable pageable) {

        Page<MedicineResponse> medicinePage = medicineBusiness.searchMedicineByPaging(pageable);
        return Api.OK(medicinePage);
    }

    @PostMapping("/upload")
    public String saveMedicines(@RequestBody List<MedicineRequest> requests) {
        medicineBusiness.saveMedicines(requests);

        return ResponseEntity.ok()
                .body("save successful")
                .toString();
    }
}
