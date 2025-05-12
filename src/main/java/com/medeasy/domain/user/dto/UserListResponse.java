package com.medeasy.domain.user.dto;

import lombok.*;

/**
 * 사용자 본인과 피보호자의 리스트
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {
    private String email;

    private String name;

    private Long userId;

    private String tag;
}
