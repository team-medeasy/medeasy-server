package com.medeasy.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSwitchRequest {
    private Long careReceiverUserId;
}
