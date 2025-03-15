package com.medeasy.domain.routine_medicine.dto;

import com.medeasy.domain.routine.db.RoutineEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineMedicineDto {
    private Long id;

    private String nickname;

    private Boolean isTaken;

    private int dose;

    private String medicineId;
}
