package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDto;
import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.repository.ChallengeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ChallengeService {

    private ChallengeRepository challengeRepo;
    private UnsavedChallengeListMapper toEntity;

    public ChallengeService(ChallengeRepository challengeRepo, UnsavedChallengeListMapper toEntity) {
        this.challengeRepo = challengeRepo;
        this.toEntity = toEntity;
    }

    @Transactional
    public ChallengeDetailDto saveNew(String username, ChallengeDto challenge) {

        // The computed value for timeAsleep isn't calculated until the transaction is closed
        // so the entity does not have the correct value here.
        var entity = challengeRepo.save(toEntity.toUnsavedEntity(username, challenge));
        return new ChallengeDetailDto(entity.getId(), entity.getName(), entity.getDescription(), entity.getStart(), entity.getFinish());
    }

    @Transactional
    public void update(String username, Long challengeId, ChallengeDto dto) {

        var entity = challengeRepo.findByUser(username, challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge not found"));

        entity.setChallengeData(dto.name(), dto.description(), dto.start(), dto.finish());
    }

    @Transactional(readOnly = true)
    public ChallengeDto retrieve(String username, Long challengeId) {
        return challengeRepo.findDtoBy(username, challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Challenge not found"));
    }

    @Transactional
    public void remove(String publicId, Long challengeId) {
        challengeRepo.findByUser(publicId, challengeId)
                .ifPresentOrElse(challengeRepo::delete, () -> {
                    throw new EntityNotFoundException("No challenge with id " + challengeId + " found for user " + publicId);
                });
    }

    @Transactional(readOnly = true)
    public List<ChallengeDetailDto> getChallenges(String username) {
        return challengeRepo.findAllByUser(username);
    }

}
