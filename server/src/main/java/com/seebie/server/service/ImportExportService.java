package com.seebie.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.dto.UserData;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImportExportService {


    // We should generally avoid anonymous inner classes, they reference the outer class and can cause memory leaks
    // However this is never recreated, so it's not a problem here
    private final static TypeReference<UserData> USER_DATA_TYPE = new TypeReference<>() {};

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;

    public ImportExportService(SleepRepository sleepRepository,  UnsavedSleepListMapper entityMapper) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
    }

    @Transactional(readOnly = true)
    public UserData retrieveUserData(String username) {

        var sleepData = sleepRepository.findAllByUsername(username).stream()
                .map(SleepDetails::sleepData)
                .toList();

        return new UserData(sleepData, List.of());
    }

    @Transactional
    public long saveUserData(String username, UserData parsedData) {
        var sleepEntities = entityMapper.apply(username, parsedData.sleepData());
        return sleepRepository.saveAll(sleepEntities).size();
    }


}
