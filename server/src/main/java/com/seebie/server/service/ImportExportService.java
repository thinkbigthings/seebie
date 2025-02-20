package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.dto.UserData;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.repository.SleepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ImportExportService {

    private SleepRepository sleepRepository;
    private ChallengeRepository challengeRepository;
    private UnsavedSleepListMapper toUnsavedSleepEntity;
    private UnsavedChallengeListMapper toUnsavedChallengeEntity;

    public ImportExportService(SleepRepository sleepRepository,  ChallengeRepository challengeRepository,
                               UnsavedSleepListMapper entityMapper, UnsavedChallengeListMapper challengeMapper) {
        this.sleepRepository = sleepRepository;
        this.challengeRepository = challengeRepository;
        this.toUnsavedSleepEntity = entityMapper;
        this.toUnsavedChallengeEntity = challengeMapper;
    }

    @Transactional(readOnly = true)
    public UserData retrieveUserData(UUID publicId) {

        var sleepData = sleepRepository.findAllByUser(publicId).stream()
                .map(SleepDetails::sleepData)
                .toList();

        var challengeData = challengeRepository.findAllByUser(publicId).stream()
                .map(ChallengeDetailDto::challenge)
                .toList();

        return new UserData(sleepData, challengeData);
    }

    @Transactional
    public long saveUserData(UUID publicId, UserData parsedData) {

        var sleepEntities = toUnsavedSleepEntity.apply(publicId, parsedData.sleepData());
        var challengeEntities = toUnsavedChallengeEntity.apply(publicId, parsedData.challengeData());

        challengeRepository.saveAll(challengeEntities);
        return sleepRepository.saveAll(sleepEntities).size();
    }

    @Transactional(readOnly = true)
    public List<SleepDetails> retrieveSleepDetails(UUID publicId) {
        return sleepRepository.findAllByUser(publicId);
    }

    @Transactional
    public long saveSleepData(UUID publicId, List<SleepData> parsedData) {

        var entityList = toUnsavedSleepEntity.apply(publicId, parsedData);
        return sleepRepository.saveAll(entityList).size();
    }

}
