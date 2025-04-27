package com.medeasy.domain.interested_medicine.db;

import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "interested_medicine")
@SequenceGenerator(
        name = "interested_medicine_seq_generator",
        sequenceName = "interested_medicine_seq",
        allocationSize = 30
)
public class InterestedMedicineEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "interested_medicine_seq_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(length = 50, nullable = false)
    private String medicineId;
}
