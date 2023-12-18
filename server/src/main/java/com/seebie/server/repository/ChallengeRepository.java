package com.seebie.server.repository;

import com.seebie.server.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.Challenge(e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.username=:username
            ORDER BY e.finish ASC 
            """)
    List<com.seebie.server.dto.Challenge> findAllByUsername(String username);

}
