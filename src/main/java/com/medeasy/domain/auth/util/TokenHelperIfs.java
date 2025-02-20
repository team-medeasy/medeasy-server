package com.medeasy.domain.auth.util;

import com.medeasy.domain.auth.dto.TokenDto;

import java.util.Map;

public interface TokenHelperIfs {

    TokenDto issueAcessToken(Map<String, Object> data);

    TokenDto issueRefreshToken(Map<String, Object> data);

    TokenDto recreateAccessToken(String refreshToken);

    Map<String, Object> validationTokenWithThrow(String token);
}
