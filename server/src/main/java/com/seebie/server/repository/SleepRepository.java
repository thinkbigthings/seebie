package com.seebie.server.repository;

import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.entity.SleepSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SleepRepository extends JpaRepository<SleepSession, Long> {

    // use both publicId and sleep id in the query to ensure the given user owns this sleep
    @Query("""
            SELECT s FROM SleepSession s
            WHERE s.user.publicId=:publicId
            AND s.id=:sleepId
            """)
    Optional<SleepSession> findBy(UUID publicId, Long sleepId);

    // use both publicId and sleep id in the query to ensure the given user owns this sleep
    @Query("""
            SELECT new com.seebie.server.dto.SleepDetails(s.id, s.minutesAsleep, s.notes, s.minutesAwake, s.startTime, s.stopTime, s.zoneId)
            FROM SleepSession s
            WHERE s.user.publicId=:publicId
            ORDER BY s.stopTime DESC
            """)
    Page<SleepDetails> loadSummaries(UUID publicId, Pageable page);

    // use both publicId and sleep id in the query to ensure the given user owns this sleep
    @Query("""
            SELECT new com.seebie.server.dto.SleepDataPoint(s.stopTime, s.minutesAsleep, s.zoneId)
            FROM SleepSession s
            WHERE s.user.publicId=:publicId
            AND s.stopTime >= :from
            AND s.stopTime <= :to
            ORDER BY s.stopTime ASC
            """)
    List<SleepDataPoint> loadChartData(UUID publicId, LocalDateTime from, LocalDateTime to);

    @Query("""
            SELECT s.minutesAsleep
            FROM SleepSession s
            WHERE s.user.publicId=:publicId
            AND s.stopTime >= :from
            AND s.stopTime <= :to
            ORDER BY s.stopTime ASC
            """)
    List<Long> loadDurations(UUID publicId, LocalDateTime from, LocalDateTime to);

    @Query("""
            SELECT new com.seebie.server.dto.SleepDetails(s.id, s.minutesAsleep, s.notes, s.minutesAwake, s.startTime, s.stopTime, s.zoneId)
            FROM SleepSession s
            WHERE s.user.publicId=:publicId
            ORDER BY s.stopTime ASC
            """)
    List<SleepDetails> findAllByUser(UUID publicId);

    int countByUser_PublicId(UUID userPublicId);
}
