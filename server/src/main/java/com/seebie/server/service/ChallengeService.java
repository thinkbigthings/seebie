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
    public void saveNewChallenge(Challenge challenge, String username) {

        userRepo.findByUsername(username)
                .map(user -> toEntity.apply(user, challenge))
                .map(challengeRepo::save)
                .orElseThrow(() -> new EntityNotFoundException(STR."No user found: \{username}"));
    }

    @Transactional(readOnly = true)
    public ChallengeList getChallenges(String username, LocalDate today) {

        var challenges = challengeRepo.findAllByUsername(username);
        var current = challenges.stream()
                .filter(c -> c.finish().isAfter(today) && c.start().isBefore(today))
                .findFirst().orElse(null);
        var completed = challenges.stream().filter(c -> c.finish().isBefore(today)).toList();
        var upcoming = challenges.stream().filter(c -> c.start().isAfter(today)).toList();

        return new ChallengeList(current, completed, upcoming);
    }

}
