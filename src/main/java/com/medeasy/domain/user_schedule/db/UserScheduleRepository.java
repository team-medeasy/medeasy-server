package com.medeasy.domain.user_schedule.db;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserScheduleRepository extends JpaRepository<UserScheduleEntity, Long> {
    List<UserScheduleEntity> findAllByIdInOrderByTakeTimeAsc(List<Long> ids);
}
