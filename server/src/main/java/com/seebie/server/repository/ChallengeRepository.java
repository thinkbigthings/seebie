package com.seebie.server.repository;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.ChallengeDetailDto(e.id, e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.username=:username
            ORDER BY e.finish ASC
            """)
    List<ChallengeDetailDto> findAllByUsername(String username);

    @Query("""
            SELECT new com.seebie.server.dto.ChallengeDto(e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.username=:username
            AND e.id=:challengeId
            """)
    Optional<ChallengeDto> findDtoBy(String username, Long challengeId);

    @Query("""
            SELECT e
            FROM Challenge e
            WHERE e.user.username=:username
            AND e.id=:challengeId
            """)
    Optional<Challenge> findByUsername(String username, Long challengeId);
}
