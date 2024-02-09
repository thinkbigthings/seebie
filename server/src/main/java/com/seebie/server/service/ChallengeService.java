package com.seebie.server.service;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.seebie.server.service.ChallengeService.ChallengeCategory.*;
import static java.util.stream.Collectors.groupingBy;


@Service
public class ChallengeService {

    private UserRepository userRepo;
    private ChallengeRepository challengeRepo;
    private UnsavedChallengeListMapper toEntity;

    public ChallengeService(UserRepository userRepo, ChallengeRepository challengeRepo, UnsavedChallengeListMapper toEntity) {
        this.userRepo = userRepo;
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
        return sortChallenges(challengeRepo.findAllByUsername(username), today);
    }

    public ChallengeList sortChallenges(List<ChallengeDetails> challenges, LocalDate today) {

        var groupedChallenges = challenges.stream().collect(groupingBy(c -> categorize(c.challenge(), today)));

        List<ChallengeDetails> completed = groupedChallenges.getOrDefault(COMPLETED, List.of());
        List<ChallengeDetails> upcoming = groupedChallenges.getOrDefault(UPCOMING, List.of());
        List<ChallengeDetails> current = groupedChallenges.getOrDefault(CURRENT, List.of());

        return new ChallengeList(current, completed, upcoming);
    }

    public enum ChallengeCategory {
        COMPLETED, UPCOMING, CURRENT;
    }

    public ChallengeCategory categorize(Challenge challenge, LocalDate today) {
        if (challenge.finish().isBefore(today)) {
            return COMPLETED;
        }
        if (challenge.start().isAfter(today)) {
            return UPCOMING;
        }
        return CURRENT;
    }

}
