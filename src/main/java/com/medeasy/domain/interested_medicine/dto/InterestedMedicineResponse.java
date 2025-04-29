package com.medeasy.domain.interested_medicine.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestedMedicineResponse {
    private Long interestedMedicineId;
    private String entpName;
    private String itemName;
    private String className;
    private String etcOtcName;
    private String itemImage;
    private String medicineId;
}
