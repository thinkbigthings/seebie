package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SleepService {

    private SleepRepository sleepRepository;
    private UnsavedSleepMapper toNewEntity;

    public SleepService(SleepRepository sleepRepository, UnsavedSleepMapper toEntity) {
        this.sleepRepository = sleepRepository;
        this.toNewEntity = toEntity;
    }

    public void saveNew(String username, SleepData dto) {
        sleepRepository.save(toNewEntity.apply(username, dto));
    }

    @Transactional(readOnly = true)
    public Page<SleepData> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(page, username);
    }

    @Transactional
    public void update(String username, Long sleepId, SleepData dto) {

        var entity = sleepRepository.findBy(username, sleepId);

//        entity.setSleepData(dto.dateAwakened(), dto.minutes(), dto.outOfBed(), dto.notes(), tagMapper.apply(dto.tags()));


    }
}
