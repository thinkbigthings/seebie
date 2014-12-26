package org.thinkbigthings.boot.service;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;
import org.thinkbigthings.sleep.SleepSessionGroupings;
import org.thinkbigthings.sleep.SleepSessionGroupings.GroupSize;
import org.thinkbigthings.sleep.SleepStatistics;

@Service
public class SleepService  {

    private final UserRepository userRepository;
    private final SleepSessionRepository sleepRepository;

    @Inject
    public SleepService(SleepSessionRepository sr, UserRepository ur) {
        userRepository = ur;
        sleepRepository = sr;
    }

    @Transactional
    public Sleep createSleepSession(Long userId, SleepResource sleepData) {
        User user = userRepository.findOne(userId);
        Sleep sleep = new Sleep(user, sleepData);
        Sleep saved = sleepRepository.save(sleep);
        return saved;
    }
    
    @Transactional(readOnly = true)
    public Page<Sleep> getSleepAverages(Long forUserId, Pageable pageable, GroupSize averages) {
        List<? extends SleepStatistics> all = sleepRepository.findAllByUserId(forUserId);
        
        // may be able to do this with a clever SQL query
        
//        SleepSessionGroupings groupings = new SleepSessionGroupings(all);
//        List<SleepStatistics> allStats = groupings.calculateAveragesByGroup(averages);
//        Page<Sleep> page = new PageImpl(allStats);
//        return page;
        
        return null;
    }
    
    @Transactional(readOnly = true)
    public Page<Sleep> getSleepSessions(Long forUserId, Pageable pageable) {
        return sleepRepository.findByUserId(forUserId, pageable);
    }

    @Transactional
    public Boolean deleteSleepSession(Long sleepId) {
        sleepRepository.delete(sleepId);
        return true;
    }

    @Transactional(readOnly = true)
    public Sleep getSleepSession(Long userId, Long sleepId) {
        Sleep sleep = sleepRepository.findOneByUserIdAndId(userId, sleepId);
        if(sleep == null) {
            throw new EntityNotFoundException("Sleep data with id was not found: " + sleepId);
        }
        return sleep;
    }

    @Transactional
    public Sleep updateSleepResource(Long userId, Long sleepId, SleepResource session) {
        Sleep persistedSleep = getSleepSession(userId, sleepId);
        persistedSleep.setMinutesAwakeInBed(session.getMinutesAwakeInBed());
        persistedSleep.setMinutesAwakeNotInBed(session.getMinutesAwakeNotInBed());
        persistedSleep.setMinutesNapping(session.getMinutesNapping());
        persistedSleep.setMinutesTotal(session.getAllMinutes());
        persistedSleep = sleepRepository.save(persistedSleep);
        return persistedSleep;
    }
    
}
