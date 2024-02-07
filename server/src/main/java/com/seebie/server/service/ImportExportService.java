package com.seebie.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.dto.UserData;
import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
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

    // We should generally avoid anonymous inner classes, they reference the outer class and can cause memory leaks
    // However this is never recreated, so it's not a problem here
    private final static TypeReference<UserData> USER_DATA_TYPE = new TypeReference<>() {};

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;

    private CsvToSleepData fromCsv;
    private SleepDetailsToCsv toCsv;

    public ImportExportService(SleepRepository sleepRepository,  UnsavedSleepListMapper entityMapper, CsvToSleepData fromCsv, SleepDetailsToCsv toCsv) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
        this.fromCsv = fromCsv;
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
    public long saveCsv(String username, String csvData) {

        LOG.info("Parsing data...");
        List<SleepData> parsedData = fromCsv.apply(csvData);
        var entityList = entityMapper.apply(username, parsedData);

        LOG.info("Saving data... ");
        int count = sleepRepository.saveAll(entityList).size();

        return count;
    }
}
