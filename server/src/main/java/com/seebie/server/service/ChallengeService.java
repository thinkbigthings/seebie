package com.seebie.server.service;

import com.seebie.server.dto.Challenge;
import com.seebie.server.entity.User;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ChallengeService {

    private UserRepository userRepo;
    private ChallengeRepository challengeRepo;


    public ChallengeService(UserRepository userRepo, ChallengeRepository challengeRepo) {
        this.userRepo = userRepo;
        this.challengeRepo = challengeRepo;
    }

    @Transactional
    public void saveNewChallenge(Challenge challenge, String username) {

        userRepo.findByUsername(username)
                .map(user -> toChallengeEntity(challenge, user))
                .map(challengeRepo::save)
                .orElseThrow(() -> new EntityNotFoundException(STR."No user found: \{username}"));
    }

    @Transactional(readOnly = true)
    public List<Challenge> getChallenges(String username) {
        return challengeRepo.findAllByUsername(username);
    }

    private com.seebie.server.entity.Challenge toChallengeEntity(Challenge challenge, User user) {
        return new com.seebie.server.entity.Challenge(challenge.name(), challenge.description(), challenge.start(),
                challenge.finish(), user);
    }

}
