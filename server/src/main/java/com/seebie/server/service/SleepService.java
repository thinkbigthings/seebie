package com.seebie.server.service;

import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
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

    private final SleepRepository sleepRepository;
    private final UnsavedSleepListMapper entityMapper;

    private final SleepMapper sleepMapper = new SleepMapper();


    public SleepService(SleepRepository sleepRepository, UnsavedSleepListMapper entityMapper) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
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
        return sleepMapper.apply(entity);
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

        entity.setSleepData(dto.minutesAwake(), dto.notes(), dto.startTime(), dto.stopTime(), dto.zoneId());
    }

    @Transactional(readOnly = true)
    public SleepDetails retrieve(String username, Long sleepId) {

        return sleepRepository.findBy(username, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));
    }

    @Transactional(readOnly = true)
    public List<SleepDataPoint> listChartData(String username, ZonedDateTime from, ZonedDateTime to) {
         return sleepRepository.loadChartData(username, from, to);
    }

    @Transactional(readOnly = true)
    public List<FilterResult> listSleepAmounts(String username, List<DateRange> filters) {

        return filters.stream()
                .map(dateRange -> new FilterResult(dateRange, sleepRepository.loadDurations(username, dateRange.from(), dateRange.to())))
                .toList();
    }

}
