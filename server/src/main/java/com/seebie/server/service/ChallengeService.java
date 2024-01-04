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
import java.util.ArrayList;
import java.util.List;


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

    /**
     * The status is according to the challenge dates relative to the current date:
     * challenge end date is in the past means it's completed,
     * challenge start date in the future means it's upcoming,
     * challenge start and end enclose the current date means it's in progress.
     */
    public ChallengeList sortChallenges(List<Challenge> challenges, LocalDate today) {

        var completed = challenges.stream().filter(c -> c.finish().isBefore(today)).toList();
        var upcoming = challenges.stream().filter(c -> c.start().isAfter(today)).toList();

        var current = new ArrayList<>(challenges);
        current.removeAll(completed);
        current.removeAll(upcoming);

        return new ChallengeList(current, completed, upcoming);
    }

}
