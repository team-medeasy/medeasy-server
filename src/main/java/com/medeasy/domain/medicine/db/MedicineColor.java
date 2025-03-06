package com.medeasy.domain.medicine.db;

import lombok.Getter;

/**
 * 갈색 분홍 초록 주황 연두 파랑
 *
 * */
@Getter
public enum MedicineColor {

    RED("빨강"),
    YELLOW("노랑"),
    WHITE("하양"),
    BROWN("갈색"),
    PINK("분홍"),
    GREEN("초록"),
    ORANGE("주황"),
    LIGHT_GREEN("연두"),
    BLUE("파랑");

    private String color;

    private MedicineColor(String color) {
        this.color = color;
    }
}
