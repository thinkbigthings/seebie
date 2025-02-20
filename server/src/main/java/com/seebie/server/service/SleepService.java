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
import java.util.UUID;

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
    public PagedModel<SleepDetails> listSleepData(UUID publicId, Pageable page) {
        return new PagedModel<>(sleepRepository.loadSummaries(publicId, page));
    }

    @Transactional
    public SleepDetails saveNew(UUID publicId, SleepData dto) {

        // The computed value for timeAsleep isn't calculated until the transaction is closed
        // so the entity does not have the correct value here.
        var entity = sleepRepository.save(entityMapper.toUnsavedEntity(publicId, dto));
        return sleepMapper.apply(entity);
    }

    @Transactional
    public void remove(UUID publicId, Long sleepId) {

        var entity = sleepRepository.findBy(publicId, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        sleepRepository.delete(entity);
    }

    @Transactional
    public void update(UUID publicId, Long sleepId, SleepData dto) {

        var entity = sleepRepository.findBy(publicId, sleepId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));

        entity.setSleepData(dto.minutesAwake(), dto.notes(), dto.startTime(), dto.stopTime(), dto.minutesAsleep(), dto.zoneId());
    }

    @Transactional(readOnly = true)
    public SleepDetails retrieve(UUID publicId, Long sleepId) {

        return sleepRepository.findBy(publicId, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found for user " + publicId + " and sleep id " + sleepId));
    }

    @Transactional(readOnly = true)
    public List<SleepDataPoint> listChartData(UUID publicId, LocalDate from, LocalDate to) {
         return sleepRepository.loadChartData(publicId, atStartOfDay(from), atEndOfDay(to));
    }

    @Transactional(readOnly = true)
    public List<List<Long>> listSleepAmounts(UUID publicId, List<DateRange> filters) {

        return filters.stream()
                .map(dateRange -> sleepRepository.loadDurations(publicId,
                        atStartOfDay(dateRange.from()),
                        atEndOfDay(dateRange.to())))
                .toList();
    }

}
