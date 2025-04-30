package com.medeasy.domain.interested_medicine.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IsInterestedMedicineResponse {

    private Long interestedMedicineId;

    private Boolean isInterestedMedicine;
}
