package com.seebie.server.service;

import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv;
import com.seebie.server.mapper.dtotoentity.TagMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SleepService {

    private static Logger LOG = LoggerFactory.getLogger(SleepService.class);

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;
    private TagMapper tagMapper;

    private SleepMapper sleepMapper = new SleepMapper();

    private CsvToSleepData fromCsv;
    private SleepDetailsToCsv toCsv;

    public SleepService(SleepRepository sleepRepository, TagMapper tagMapper, UnsavedSleepListMapper entityMapper, CsvToSleepData fromCsv, SleepDetailsToCsv toCsv) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
        this.tagMapper = tagMapper;
        this.fromCsv = fromCsv;
        this.toCsv = toCsv;
    }

    @Transactional(readOnly = true)
    public Page<SleepDetails> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(username, page);
    }

    @Transactional
    public SleepDetails saveNew(String username, SleepData dto) {

        // The computed value for timeAsleep isn't calculated until the transaction is closed
        // so the entity does not have the correct value here.
        var entity = sleepRepository.save(entityMapper.toUnsavedEntity(username, dto));
        return new SleepDetails(entity.getId(), entity.getMinutesAsleep(), sleepMapper.apply(entity));
    }

    @Transactional
    public void remove(String username, Long sleepId) {

        var entity = sleepRepository.findBy(username, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        sleepRepository.delete(entity);
    }

    @Transactional
    public void update(String username, Long sleepId, SleepData dto) {

        var entity = sleepRepository.findBy(username, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        entity.setSleepData(dto.minutesAwake(), dto.notes(), tagMapper.apply(dto.tags()), dto.startTime(), dto.stopTime(), dto.zoneId());
    }

    @Transactional(readOnly = true)
    public SleepData retrieve(String username, Long sleepId) {

        return sleepRepository.findBy(username, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));
    }

    @Transactional(readOnly = true)
    public List<SleepDataPoint> listChartData(String username, ZonedDateTime from, ZonedDateTime to) {
         return sleepRepository.loadChartData(username, from, to);
    }

    @Transactional(readOnly = true)
    public List<List<Integer>> listSleepAmounts(String username, FilterList filters) {

        // alternatively, look into database "group by" with Postgres / JPQL
        return filters.dataFilters().stream()
                .map(dateRange -> sleepRepository.loadDurations(username, dateRange.from(), dateRange.to()))
                .toList();
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
