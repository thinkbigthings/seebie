package com.seebie.server.repository;

import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.entity.SleepSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


public interface SleepRepository extends JpaRepository<SleepSession, Long> {

    @Query("SELECT s FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "AND s.id=:sleepId ")
    Optional<SleepSession> findBy(String username, Long sleepId);

    // use both username and sleep id in the query to ensure the given user owns this sleep
    @Query("SELECT new com.seebie.server.dto.SleepDataWithId" +
            "(s.id, s.notes, s.outOfBed, s.startTime, s.stopTime) " +
            "FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "ORDER BY s.stopTime DESC ")
    Page<SleepDataWithId> loadSummaries(Pageable page, String username);

    // use both username and sleep id in the query to ensure the given user owns this sleep
    @Query("SELECT new com.seebie.server.dto.SleepDataPoint" +
            "(s.stopTime, s.durationMinutes) " +
            "FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "AND s.stopTime >= :from " +
            "AND s.stopTime <= :to " +
            "ORDER BY s.stopTime ASC ")
    List<SleepDataPoint> loadChartData(String username, ZonedDateTime from, ZonedDateTime to);
}
