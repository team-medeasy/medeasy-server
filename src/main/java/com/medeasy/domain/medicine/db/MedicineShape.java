package com.medeasy.domain.medicine.db;

import lombok.Getter;

@Getter
public enum MedicineShape {

    CIRCLE("원형"),
    OVAL("타원형"),
    TRIANGLE("삼각형"),
    RECTANGLE("사각형"),
    CAPSULE("캡슐형"),
    HEXAGON("육각형"),
    PENTAGON("오각형"),
    HALF_MOON("반원형"),
    DIAMOND("마름모형"),
    OBLONG("장방형")
    ;

    private String shape;

    private MedicineShape(String shape) {
        this.shape = shape;
    }
}
