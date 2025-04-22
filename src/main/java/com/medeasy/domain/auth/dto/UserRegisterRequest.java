package com.medeasy.domain.auth.dto;

import com.medeasy.domain.user.db.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor // 요청이 들어왔을 때  json -> 객체 변환 위함
@Setter
@Builder
public class UserRegisterRequest {

    @NotBlank
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "abcd1234")
    private String password;

    @NotBlank
    @Schema(description = "사용자 이름", example = "김한성")
    private String name;

    @Schema(description = "사용자 생년월일", example = " 2025-02-20")
    private Date birthday;

    @Schema(description = "사용자 성별", example = "MALE, FEMALE")
    private Gender gender;
}
