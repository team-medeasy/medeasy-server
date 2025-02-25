package com.medeasy.domain.medicine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MedicineUpdateRequest {
    @JsonProperty("ITEM_SEQ")
    private String itemSeq;

    @JsonProperty("DRUG_SHAPE")
    private String shape;

    @JsonProperty("COLOR_CLASS1")
    private String colorClass1;

    @JsonProperty("COLOR_CLASS2")
    private String colorClass2;
}
