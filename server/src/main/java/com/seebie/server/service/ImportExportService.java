package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDetails;
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
    public UserData retrieveUserData(String username) {

        var sleepData = sleepRepository.findAllByUsername(username).stream()
                .map(SleepDetails::sleepData)
                .toList();

        var challengeData = challengeRepository.findAllByUsername(username).stream()
                .map(ChallengeDetails::challenge)
                .toList();

        return new UserData(sleepData, challengeData);
    }

    @Transactional
    public long saveUserData(String username, UserData parsedData) {

        var sleepEntities = toUnsavedSleepEntity.apply(username, parsedData.sleepData());
        var challengeEntities = toUnsavedChallengeEntity.apply(username, parsedData.challengeData());

        challengeRepository.saveAll(challengeEntities);
        return sleepRepository.saveAll(sleepEntities).size();
    }

    @Transactional(readOnly = true)
    public List<SleepDetails> retrieveSleepDetails(String username) {
        return sleepRepository.findAllByUsername(username);
    }

    @Transactional
    public long saveSleepData(String username, List<SleepData> parsedData) {

        var entityList = toUnsavedSleepEntity.apply(username, parsedData);
        return sleepRepository.saveAll(entityList).size();
    }

}
