package com.seebie.server.repository;


import com.seebie.server.dto.Sleep;
import com.seebie.server.entity.SleepSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SleepRepository extends JpaRepository<SleepSession, Long> {

    @Query("SELECT new com.seebie.server.dto.Sleep" +
            "(s.dateAwakened, s.minutes, s.notes, s.outOfBed) " +
            "FROM SleepSession s " +
            "WHERE s.user.username=:username " +
            "ORDER BY s.dateAwakened DESC ")
    Page<Sleep> loadSummaries(Pageable page, String username);
}
