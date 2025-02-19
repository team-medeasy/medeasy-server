package com.medeasy.domain.user.dto;

import com.medeasy.domain.user.db.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor // 요청이 들어왔을 때  json -> 객체 변환 위함
@Setter
public class UserRegisterRequest {

    private String email;

    private String password;

    private String name;

    private Date birthday;

    private Gender gender;
}
