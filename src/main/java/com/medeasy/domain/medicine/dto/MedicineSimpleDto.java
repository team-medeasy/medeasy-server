package com.medeasy.domain.medicine.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineSimpleDto {

    private String medicineId;

    private String entpName;

    private String medicineName;

    private String className;

    private String itemImage;
}
