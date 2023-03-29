package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataPoint;
import com.seebie.server.dto.SleepDetails;
import com.seebie.server.mapper.dtotoentity.TagMapper;
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

    private SleepRepository sleepRepository;
    private UnsavedSleepListMapper entityMapper;
    private TagMapper tagMapper;

    private SleepMapper sleepMapper = new SleepMapper();

    public SleepService(SleepRepository sleepRepository, TagMapper tagMapper, UnsavedSleepListMapper entityMapper) {
        this.sleepRepository = sleepRepository;
        this.entityMapper = entityMapper;
        this.tagMapper = tagMapper;
    }

    @Transactional(readOnly = true)
    public Page<SleepDetails> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(username, page);
    }

    @Transactional
    public SleepDetails saveNew(String username, SleepData dto) {
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

        entity.setSleepData(dto.minutesAwake(), dto.notes(), tagMapper.apply(dto.tags()), dto.startTime(), dto.stopTime());
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
    public List<SleepDetails> retrieveAll(String username) {
        return sleepRepository.findAllByUsername(username);
    }

    @Transactional
    public long saveNew(String username, List<SleepData> dtoList) {
        var entityList = entityMapper.apply(username, dtoList);
        return sleepRepository.saveAll(entityList).size();
    }

}
