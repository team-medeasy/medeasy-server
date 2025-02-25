package com.medeasy.domain.medicine.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.medicine.business.MedicineBusiness;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public Api<List<MedicineResponse>> searchMedicines(
            @RequestParam("medicine_name")
            @Parameter(description = "약 이름 키워드", required = true)
            String medicineName,
            @RequestParam(value = "size", defaultValue = "10")
            @Parameter(description = "불러올 데이터 개수", required = false)
            int size
    ) {
        List<MedicineResponse> medicineResponses= medicineBusiness.searchMedicines(medicineName, size);

        return Api.OK(medicineResponses);
    }

    // 약 데이터 추가 컬럼
    @PostMapping("/update")
    @Operation(summary = "약 json 리스트 저장", description = "개발 중 약 데이터 저장용 API")
    public String updateMedicines(
            @Valid
            @RequestBody List<MedicineUpdateRequest> requests
    ) {
        medicineBusiness.updateMedicines(requests);

        return ResponseEntity.ok()
                .body("updated successful")
                .toString();
    }
}
