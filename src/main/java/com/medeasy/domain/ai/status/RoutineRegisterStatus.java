package com.medeasy.domain.ai.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum RoutineRegisterStatus implements ChatStatusIfs{
    DEFAULT_ROUTINE_REGISTER("default_routine_register", "기본 루틴 등록 기능", 2),
    PRESCRIPTION_ROUTINE_REGISTER("prescription_routine_register", "처방전 루틴 등록", 2),
    PILL_PHOTO_ROUTINE_REGISTER("pill_photo_routine_register", "알약 촬영 루틴 등록", 2)
    ;

    private final String intent;
    private final String description;
    private final Integer level;

    public static Optional<RoutineRegisterStatus> fromIntent(String intent) {
        return Arrays.stream(RoutineRegisterStatus.values())
                .filter(status -> status.getIntent().equalsIgnoreCase(intent))
                .findFirst();
    }
}
