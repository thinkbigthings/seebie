package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.dto.UserData;
import com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImportExportService {

    private static Logger LOG = LoggerFactory.getLogger(ImportExportService.class);

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;

    private SleepDetailsToCsv toCsv;

    public ImportExportService(SleepRepository sleepRepository,  UnsavedSleepListMapper entityMapper, SleepDetailsToCsv toCsv) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
        this.toCsv = toCsv;
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

    @Transactional(readOnly = true)
    public String retrieveCsv(String username) {
        return toCsv.apply(sleepRepository.findAllByUsername(username));
    }

    @Transactional
    public long saveCsv(String username, List<SleepData> parsedData) {

        var entityList = entityMapper.apply(username, parsedData);

        LOG.info("Saving data... ");
        int count = sleepRepository.saveAll(entityList).size();

        return count;
    }

}
