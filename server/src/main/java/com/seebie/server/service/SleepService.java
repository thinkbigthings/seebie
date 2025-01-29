package com.seebie.server.service;

import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.seebie.server.dto.DateRange.atEndOfDay;
import static com.seebie.server.dto.DateRange.atStartOfDay;

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
    public PagedModel<SleepDetails> listSleepData(String username, Pageable page) {
        return new PagedModel<>(sleepRepository.loadSummaries(username, page));
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

        entity.setSleepData(dto.minutesAwake(), dto.notes(), dto.startTime(), dto.stopTime(), dto.minutesAsleep(), dto.zoneId());
    }

    @Transactional(readOnly = true)
    public SleepDetails retrieve(String username, Long sleepId) {

        return sleepRepository.findBy(username, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found for user " + username + " and sleep id " + sleepId));
    }

    @Transactional(readOnly = true)
    public List<SleepDataPoint> listChartData(String username, LocalDate from, LocalDate to) {
         return sleepRepository.loadChartData(username, atStartOfDay(from), atEndOfDay(to));
    }

    @Transactional(readOnly = true)
    public List<List<Long>> listSleepAmounts(String username, List<DateRange> filters) {

        return filters.stream()
                .map(dateRange -> sleepRepository.loadDurations(username,
                        atStartOfDay(dateRange.from()),
                        atEndOfDay(dateRange.to())))
                .toList();
    }

}
