package com.medeasy.domain.chat.db;

import java.util.Optional;

public interface UserSessionRepository {
    void save(UserSession session);
    Optional<UserSession> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
