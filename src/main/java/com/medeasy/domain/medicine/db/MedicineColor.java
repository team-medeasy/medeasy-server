package com.medeasy.domain.medicine.db;

import lombok.Getter;

/**
 * 약 색상 enum 타입
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
    BLUE("파랑"),
    PURPLE("보라"),
    GRAY("회색"),
    CYAN("청록"),
    NAVY("남색"),
    MAGENTA("자주"),
    BLACK("검정"),
    TRANSPARENT("투명");

    private final String color;

    MedicineColor(String color) {
        this.color = color;
    }
}
