package com.medeasy.domain.user.db;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 150)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Gender gender;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "registered_at")
    private Date registeredAt;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "logined_at")
    private Date loginedAt;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nok_id")
    private UserEntity nok;

    // 반대쪽 관계 (필요한 경우)
    @OneToMany(mappedBy = "nok")
    private List<UserEntity> subUsers = new ArrayList<>();
}
