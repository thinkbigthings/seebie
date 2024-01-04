package com.seebie.server.service;

import com.seebie.server.dto.Challenge;
import com.seebie.server.dto.ChallengeList;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void saveNewChallenge(String username, Challenge challenge) {

        userRepo.findByUsername(username)
                .map(user -> toEntity.apply(user, challenge))
                .map(challengeRepo::save)
                .orElseThrow(() -> new EntityNotFoundException(STR."No user found: \{username}"));
    }

    @Transactional(readOnly = true)
    public ChallengeList getChallenges(String username, LocalDate today) {
        return sortChallenges(challengeRepo.findAllByUsername(username), today);
    }

    public ChallengeList sortChallenges(List<Challenge> challenges, LocalDate today) {

        var groupedChallenges = challenges.stream().collect(groupingBy(c -> categorize(c, today)));

        List<Challenge> completed = groupedChallenges.getOrDefault(COMPLETED, List.of());
        List<Challenge> upcoming = groupedChallenges.getOrDefault(UPCOMING, List.of());
        List<Challenge> current = groupedChallenges.getOrDefault(CURRENT, List.of());

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
