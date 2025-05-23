package com.seebie.server.repository;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("""
            SELECT new com.seebie.server.dto.ChallengeDetailDto(e.id, e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.publicId=:publicId
            ORDER BY e.finish ASC
            """)
    List<ChallengeDetailDto> findAllByUser(UUID publicId);

    @Query("""
            SELECT new com.seebie.server.dto.ChallengeDetailDto(e.id, e.name, e.description, e.start, e.finish)
            FROM Challenge e
            WHERE e.user.publicId=:publicId
            AND e.id=:challengeId
            """)
    Optional<ChallengeDetailDto> findDtoBy(UUID publicId, Long challengeId);

    @Query("""
            SELECT e
            FROM Challenge e
            WHERE e.user.publicId=:publicId
            AND e.id=:challengeId
            """)
    Optional<Challenge> findByUser(UUID publicId, Long challengeId);

}
