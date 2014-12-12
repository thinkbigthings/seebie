package org.thinkbigthings.boot.service;

import java.util.List;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.sleep.SleepSessionJSON;

public interface SleepServiceInterface {
    Sleep createSleepSession(Long forUserId, SleepSessionJSON s);
    List<Sleep> getSleepSessions(Long forUserId);
    public Boolean deleteSleepSession(Long sleepId);
    public Sleep getSleepSession(Long sleepId);

    public Sleep updateSleep(Long userId, Long sleepId, SleepSessionJSON session);
}
