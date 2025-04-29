package com.medeasy.domain.chat.db;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserSessionRepository implements UserSessionRepository {
    private final Map<Long, UserSession> store = new ConcurrentHashMap<>();

    @Override
    public void save(UserSession session) {
        store.put(session.getUserId(), session);
    }

    @Override
    public Optional<UserSession> findByUserId(Long userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public void deleteByUserId(Long userId) {
        store.remove(userId);
    }
}
