package com.seebie.server.repository;

import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.ChallengeDetails(e.id, e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.username=:username
            ORDER BY e.finish ASC 
            """)
    List<ChallengeDetails> findAllByUsername(String username);

    @Query("""
            SELECT e
            FROM Challenge e
            WHERE e.user.username=:username
            AND e.id=:challengeId
            """)
    Optional<Challenge> findByUsername(String username, Long challengeId);
}
