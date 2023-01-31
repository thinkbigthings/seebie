package com.seebie.server.repository;

import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.entity.SleepSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface SleepRepository extends JpaRepository<SleepSession, Long> {

    @Query("SELECT s FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "AND s.id=:sleepId ")
    Optional<SleepSession> findBy(String username, Long sleepId);

    // use both username and sleep id in the query to ensure the given user owns this sleep
    @Query("SELECT new com.seebie.server.dto.SleepDataWithId" +
            "(s.id, s.dateAwakened, s.minutes, s.notes, s.outOfBed) " +
            "FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "ORDER BY s.dateAwakened DESC ")
    Page<SleepDataWithId> loadSummaries(Pageable page, String username);
}
