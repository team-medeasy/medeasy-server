package com.medeasy.domain.user.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserMedicinesResponse {
    private int medicineCount;

    private List<String> medicineIds;
}
