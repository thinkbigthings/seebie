package org.thinkbigthings.boot.service;

import org.thinkbigthings.boot.domain.SleepSession;

public interface SleepServiceInterface {
    SleepSession createSleepSession(Long forUserId, SleepSession s);
}
