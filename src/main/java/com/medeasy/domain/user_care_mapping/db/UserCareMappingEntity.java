package com.medeasy.domain.user_care_mapping.db;

import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_care_mapping")
@SequenceGenerator(
        name = "user_care_mapping_seq_generator",
        sequenceName = "user_care_mapping_id_seq",
        allocationSize = 1
)
public class UserCareMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_care_mapping_seq_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_giver_id")
    private UserEntity careGiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "care_receiver_id")
    private UserEntity careReceiver;

    @CreationTimestamp
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;
}


