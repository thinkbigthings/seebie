package com.seebie.server.service;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.repository.ChallengeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static com.seebie.server.dto.ChallengeList.newChallengeList;

@Service
public class ChallengeService {

    private ChallengeRepository challengeRepo;
    private UnsavedChallengeListMapper toEntity;

    public ChallengeService(ChallengeRepository challengeRepo, UnsavedChallengeListMapper toEntity) {
        this.challengeRepo = challengeRepo;
        this.toEntity = toEntity;
    }

    @Transactional
    public ChallengeDetails saveNew(String username, Challenge challenge) {

        // The computed value for timeAsleep isn't calculated until the transaction is closed
        // so the entity does not have the correct value here.
        var entity = challengeRepo.save(toEntity.toUnsavedEntity(username, challenge));
        return new ChallengeDetails(entity.getId(), entity.getName(), entity.getDescription(), entity.getStart(), entity.getFinish());
    }

    @Transactional
    public void update(String username, Long challengeId, Challenge dto) {

        var entity = challengeRepo.findByUsername(username, challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge not found"));

        entity.setChallengeData(dto.name(), dto.description(), dto.start(), dto.finish());
    }

    @Transactional(readOnly = true)
    public Challenge retrieve(String username, Long challengeId) {
        return challengeRepo.findDtoBy(username, challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge not found"));
    }

    @Transactional
    public void remove(String username, Long challengeId) {
        challengeRepo.findByUsername(username, challengeId)
                .ifPresentOrElse(challengeRepo::delete, () -> {
                    throw new EntityNotFoundException(STR."No challenge with id \{challengeId} found for user \{username}");
                });
    }

    @Transactional(readOnly = true)
    public ChallengeList getChallenges(String username, LocalDate today) {
        return newChallengeList(challengeRepo.findAllByUsername(username), today);
    }

}
