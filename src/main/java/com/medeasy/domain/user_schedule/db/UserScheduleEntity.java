package com.medeasy.domain.user_schedule.db;

import com.medeasy.domain.user.db.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_schedule")
public class UserScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "take_time")
    @Temporal(TemporalType.TIME)
    private LocalTime takeTime;
}
