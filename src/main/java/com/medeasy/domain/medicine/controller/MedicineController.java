package com.medeasy.domain.medicine.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.medicine.business.MedicineBusiness;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    // 개발 중 약 데이터 저장시 사용
    @PostMapping("/upload")
    @Operation(summary = "약 json 리스트 저장", description = "개발 중 약 데이터 저장용 API")
    public String saveMedicines(
            @Valid
            @RequestBody List<MedicineRequest> requests
    ) {
        medicineBusiness.saveMedicines(requests);

        return ResponseEntity.ok()
                .body("save successful")
                .toString();
    }

    // 약 검색 API
    @GetMapping("/search")
    @Operation(summary = "약 검색 API", description = "검색엔진 기반으로 약 이름 검색시 유사한 약 리스트 출력")
    public Api<List<MedicineResponse>> searchMedicines(@RequestParam("medicine_name") String medicineName) {
        List<MedicineResponse> medicineResponses= medicineBusiness.searchMedicines(medicineName);

        return Api.OK(medicineResponses);
    }
}
