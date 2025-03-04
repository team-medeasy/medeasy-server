package com.medeasy.domain.medicine.db;

import lombok.Getter;

@Getter
public enum MedicineColor {

    RED("빨강"), YELLOW("노랑"), WHITE("하양");

    private String color;

    private MedicineColor(String color) {
        this.color = color;
    }
}
