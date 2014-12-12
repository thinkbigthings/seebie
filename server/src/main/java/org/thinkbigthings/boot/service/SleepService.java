package org.thinkbigthings.boot.service;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;
import org.thinkbigthings.sleep.SleepSessionJSON;

@Service
class SleepService implements SleepServiceInterface {

    private final UserRepository userRepository;
    private final SleepSessionRepository sleepRepository;

    @Inject
    public SleepService(SleepSessionRepository sr, UserRepository ur) {
        userRepository = ur;
        sleepRepository = sr;
    }

    @Transactional
    @Override
    public Sleep createSleepSession(Long userId, SleepSessionJSON sleepData) {
        User user = userRepository.findOne(userId);
        Sleep sleep = new Sleep(user, sleepData);
        Sleep saved = sleepRepository.save(sleep);
        return saved;
    }

    @Transactional
    @Override
    public List<Sleep> getSleepSessions(Long forUserId) {
        User user = userRepository.findOne(forUserId);
        return sleepRepository.findByUser(user);
    }

    @Transactional
    @Override
    public Boolean deleteSleepSession(Long sleepId) {
        sleepRepository.delete(sleepId);
        return true;
    }

    @Transactional
    @Override
    public Sleep getSleepSession(Long sleepId) {
        Sleep session = sleepRepository.findOne(sleepId);
        if(session == null) {
            throw new EntityNotFoundException("Sleep data with id was not found: " + sleepId.toString());
        }
        return session;
    }

    @Transactional
    @Override
    public Sleep updateSleep(Long userId, Long sleepId, SleepSessionJSON session) {
        Sleep persistedSleep = getSleepSession(sleepId);
        persistedSleep.setMinutesAwakeInBed(session.getMinutesAwakeInBed());
        persistedSleep.setMinutesAwakeNotInBed(session.getMinutesAwakeNotInBed());
        persistedSleep.setMinutesNapping(session.getMinutesNapping());
        persistedSleep.setMinutesTotal(session.getMinutesTotal());
        persistedSleep = sleepRepository.save(persistedSleep);
        return persistedSleep;
    }
}
