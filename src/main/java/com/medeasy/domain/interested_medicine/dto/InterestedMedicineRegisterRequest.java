package com.medeasy.domain.interested_medicine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InterestedMedicineRegisterRequest {

    @NotBlank
    private String medicineId;
}
