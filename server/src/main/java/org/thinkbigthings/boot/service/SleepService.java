package org.thinkbigthings.boot.service;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.domain.SleepSession;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;

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
    public SleepSession createSleepSession(Long userId, SleepSession sleep) {
        User user = userRepository.findOne(userId);
        SleepSession saved = sleepRepository.save(sleep);
        user.getSleepSessions().add(sleep);
        userRepository.saveAndFlush(user);
        return saved;
    }
}
