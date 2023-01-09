package com.seebie.server.service;

import com.seebie.server.dto.Sleep;
import com.seebie.server.dto.UserSleep;
import com.seebie.server.mapper.dtotoentity.SleepMapper;
import com.seebie.server.repository.SleepRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SleepService {

    private SleepRepository sleepRepository;
    private SleepMapper sleepToEntity;

    public SleepService(SleepRepository sleepRepository, SleepMapper toEntity) {
        this.sleepRepository = sleepRepository;
        this.sleepToEntity = toEntity;
    }

    public void save(String username, Sleep dto) {
        sleepRepository.save(sleepToEntity.apply(new UserSleep(username, dto)));
    }

    @Transactional(readOnly = true)
    public Page<Sleep> listSleepData(String username, Pageable page) {
        return sleepRepository.loadSummaries(page, username);
    }


}
