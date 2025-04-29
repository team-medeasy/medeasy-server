package com.medeasy.domain.interested_medicine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.interested_medicine.business.InterestedMedicineBusiness;
import com.medeasy.domain.interested_medicine.dto.InterestedMedicineRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interested-medicine")
@Slf4j
public class InterestedMedicineController {

    private final InterestedMedicineBusiness interestedMedicineBusiness;

    @Operation(summary = "관심 의약품 등록 api", description =
            """
            요청 값: 
            
            medicine_id
            
            """
    )
    @PostMapping("")
    public Api<Object> registerInterestedMedicine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid @RequestBody InterestedMedicineRegisterRequest request
    ) {
        interestedMedicineBusiness.registerInterestedMedicine(userId, request);
        return Api.OK(null);
    }

    @Operation(summary = "관심 의약품 목록 조회 api", description =
            """
            요청 값: 
            
            page: 페이지네이션 적용 페이지 번호 
            
            size: 한번에 가져올 관심 의약품 개수 
            
            응답 값: 
            
            interested_medicine_id:  관심 의약품 식별자 
            
            entp_name: 제약사 이름
            
            item_name: 약품 이름
            
            class_name: 분류명
            
            etc_otc_name: 전문의약품 여부
            
            item_image: 이미지 Url
            
            medicine_id: 의약품 정보 식별자 
            
            """
    )
    @GetMapping("")
    public Api<Object> getInterestedMedicines(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(value = "page", defaultValue = "0", required = false)
            @Parameter(description = "페이지 번호(default: 0)", required = false)
            int page,

            @RequestParam(value = "size", defaultValue = "10", required = false)
            @Parameter(description = "불러올 데이터 개수 (default: 10)", required = false)
            int size
    ) {
        var response=interestedMedicineBusiness.getInterestedMedicines(userId, page, size);
        return Api.OK(response);
    }
}
