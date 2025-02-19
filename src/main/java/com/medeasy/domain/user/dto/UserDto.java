package com.medeasy.domain.user.dto;

import com.medeasy.domain.user.db.Gender;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    private String email;

    private String password;

    private String name;

    private Gender gender;

    private Date registeredAt;

    private Date loginedAt;

    private Date birthday;
}
