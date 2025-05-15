package com.medeasy.domain.user.dto;

import com.medeasy.domain.user.db.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@Setter
public class UserResponse {

    private Long id;

    private String email;

    private String name;

    private Gender gender;

    private Date registeredAt;

    private LocalDateTime loginedAt;

    private Date birthday;

    private Boolean isNotificationAgreed;
}
