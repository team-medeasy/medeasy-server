package com.medeasy.domain.routine.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutineEventService {

    @Qualifier("redisAlarmTemplate")
    private final RedisTemplate<String, RoutineCheckEvent> redisTemplate;
    private static final String ROUTINE_CHECK_QUEUE = "routine:check:events";

    /**
     * 복약 체크 이벤트 생성 및 Redis 큐에 전송
     */
    public void publishRoutineCheckEvent(Long userId, String scheduleName) {

        // 이벤트 생성
        RoutineCheckEvent event = RoutineCheckEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .scheduleName(scheduleName)
                .checkedAt(LocalDateTime.now())
                .build();

        // Redis 큐에 메시지 추가 (RPUSH)
        try {
            redisTemplate.opsForList().rightPush(ROUTINE_CHECK_QUEUE, event);
            log.info("복약 체크 이벤트가 Redis 큐에 전송되었습니다. eventId: {}", event.getEventId());
        } catch (Exception e) {
            log.error("Redis 큐에 이벤트 전송 실패: {}", e.getMessage(), e);
            // 실패 시 대체 처리 로직 (예: DB에 저장하여 나중에 재시도)
        }
    }
}
