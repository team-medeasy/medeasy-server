package com.medeasy.domain.routine.db;

import com.medeasy.domain.routine.dto.RoutineFlatDto;
import com.medeasy.domain.routine.dto.RoutineGroupDateRangeDto;
import com.medeasy.domain.routine_group.db.QRoutineGroupEntity;
import com.medeasy.domain.user_schedule.db.QUserScheduleEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoutineQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 현재 복용 중인 약 루틴의 복용기간 즉 시작 날짜와 마지막날짜를 반환
     * */
    public List<RoutineGroupDateRangeDto> findCurrentRoutineStartAndEndDateRangeByGroup(
            Long userId, LocalDate startDate, LocalDate endDate) {

        QRoutineEntity r = QRoutineEntity.routineEntity;
        QRoutineGroupEntity rg = QRoutineGroupEntity.routineGroupEntity;
        BooleanBuilder where = new BooleanBuilder();
        where.and(rg.user.id.eq(userId));

        if (startDate != null && endDate != null) {
            where.and(r.takeDate.between(startDate, endDate));
        }

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(Projections.constructor(
                        RoutineGroupDateRangeDto.class,
                        rg.id,
                        rg.updatedAt,
                        rg.medicineId,
                        r.takeDate.min(),
                        r.takeDate.max()
                ))
                .from(r)
                .join(r.routineGroup, rg)
                .where(where)
                .groupBy(rg.id, rg.updatedAt, rg.medicineId)
                .having(r.takeDate.max().goe(today)) // max_date가 오늘날짜와 같거나 큰경우
                .fetch();
    }

    /**
     * 과거 복용 중인 약 루틴의 복용기간 즉 시작 날짜와 마지막날짜를 반환
     * */
    public List<RoutineGroupDateRangeDto> findPastRoutineStartAndEndDateRangeByGroup(
            Long userId, LocalDate startDate, LocalDate endDate) {

        QRoutineEntity r = QRoutineEntity.routineEntity;
        QRoutineGroupEntity rg = QRoutineGroupEntity.routineGroupEntity;

        BooleanBuilder where = new BooleanBuilder();
        where.and(rg.user.id.eq(userId));

        if (startDate != null && endDate != null) {
            where.and(r.takeDate.between(startDate, endDate));
        }

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(Projections.constructor(
                        RoutineGroupDateRangeDto.class,
                        rg.id,
                        rg.updatedAt,
                        rg.medicineId,
                        r.takeDate.min(),
                        r.takeDate.max()
                ))
                .from(r)
                .join(r.routineGroup, rg)
                .where(where)
                .groupBy(rg.id, rg.updatedAt, rg.medicineId)
                .having(r.takeDate.max().lt(today)) // max_date가 오늘날짜보다 작은경우
                // TODO 오늘날짜여도 약을 다 먹은 경우에는 표시
                .fetch();
    }

    /**
     * routine_group_id에 해당하는 복용 중인 약 정보 가져오기
     * */
    public List<RoutineFlatDto> findRoutineInfoByUserIdAndGroupIds(Long userId, List<Long> routineGroupIds) {
        QRoutineEntity r = QRoutineEntity.routineEntity;
        QUserScheduleEntity us = QUserScheduleEntity.userScheduleEntity;
        QRoutineGroupEntity rg = QRoutineGroupEntity.routineGroupEntity;

        return queryFactory
                .select(Projections.constructor(
                        RoutineFlatDto.class,
                        rg.id,
                        r.id,
                        r.takeDate,
                        us.id,
                        rg.medicineId,
                        rg.nickname,
                        rg.dose
                ))
                .from(r)
                .join(r.userSchedule, us)
                .join(r.routineGroup, rg)
                .where(
                        rg.user.id.eq(userId),
                        rg.id.in(routineGroupIds)
                )
                .orderBy(rg.id.asc())
                .fetch();
    }
}
