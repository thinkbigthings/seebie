package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.dto.SleepDataWithId;
import com.seebie.server.mapper.dtotoentity.TagMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepMapper;
import com.seebie.server.mapper.entitytodto.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SleepService {

    private SleepRepository sleepRepository;
    private UnsavedSleepMapper toNewEntity;
    private TagMapper tagMapper;

    private SleepMapper sleepMapper = new SleepMapper();

    public SleepService(SleepRepository sleepRepository, UnsavedSleepMapper toEntity, TagMapper tagMapper) {
        this.sleepRepository = sleepRepository;
        this.toNewEntity = toEntity;
        this.tagMapper = tagMapper;
    }

    @Transactional(readOnly = true)
    public Page<SleepDataWithId> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(page, username);
    }

    @Transactional
    public SleepDataWithId saveNew(String username, SleepData dto) {
        var entity = sleepRepository.save(toNewEntity.apply(username, dto));
        return new SleepDataWithId(entity.getId(), sleepMapper.apply(entity));
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

        entity.setSleepData(dto.outOfBed(), dto.notes(), tagMapper.apply(dto.tags()), dto.startTime(), dto.stopTime());
    }

    @Transactional(readOnly = true)
    public SleepData retrieve(String username, Long sleepId) {

        return sleepRepository.findBy(username, sleepId)
                .map(sleepMapper)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sleep session not found"));
    }
}
