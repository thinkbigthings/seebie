package org.thinkbigthings.boot.service;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;

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

    @Transactional
    public Page<Sleep> getSleepSessions(Long forUserId, Pageable pageable) {
        User user = userRepository.findOne(forUserId);
        if(user == null) {
            throw new EntityNotFoundException("User with id was not found: " + forUserId);
        }
        return sleepRepository.findByUser(user, pageable);
    }

    @Transactional
    public Boolean deleteSleepSession(Long sleepId) {
        sleepRepository.delete(sleepId);
        return true;
    }

    @Transactional
    public Sleep getSleepSession(Long userId, Long sleepId) {
        User user = userRepository.findOne(userId);
        List<Sleep> sleepList = sleepRepository.findByUserAndId(user, sleepId);
        if(sleepList.isEmpty()) {
            throw new EntityNotFoundException("Sleep data with id was not found: " + sleepId);
        }
        return sleepList.get(0);
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
