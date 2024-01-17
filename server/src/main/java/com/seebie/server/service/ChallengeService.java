package com.seebie.server.service;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeMapper;
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
    private UnsavedChallengeMapper toEntity = new UnsavedChallengeMapper();

    public ChallengeService(UserRepository userRepo, ChallengeRepository challengeRepo) {
        this.userRepo = userRepo;
        this.challengeRepo = challengeRepo;
    }

    @Transactional
    public ChallengeDetails saveNewChallenge(String username, Challenge challenge) {

        var saved = userRepo.findByUsername(username)
                .map(user -> toEntity.apply(user, challenge))
                .map(challengeRepo::save)
                .orElseThrow(() -> new EntityNotFoundException(STR."No user found: \{username}"));

        return new ChallengeDetails(saved.getId(), saved.getName(), saved.getDescription(), saved.getStart(), saved.getFinish());
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
