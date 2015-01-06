package org.thinkbigthings.boot.service;

import static org.thinkbigthings.sleep.SleepStatistics.BY_TIME_ASCENDING;
import static org.thinkbigthings.sleep.SleepStatistics.BY_TIME_DESCENDING;

import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;
import org.thinkbigthings.boot.dto.SleepAveragesResource;
import org.thinkbigthings.sleep.SleepStatistics;
import org.thinkbigthings.sleep.SleepStatisticsCalculator;

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
    public Page<SleepAveragesResource> getSleepAverages(Long forUserId, Pageable pageable, SleepStatisticsCalculator.Group averages) {

        // TODO 5 would like to do this with a clever query 
        // but spring data doesn't support pagination or dynamic sorting for native queries
        // http://stackoverflow.com/questions/27666648/how-to-extract-the-week-from-a-date-in-mysql-using-arbitrary-weekday-as-the-star
        // could still do just to get the averages and do the paging myself since I do the paging myself anyways
        // also logic for statistics calculator could be inside custom repository implementation
        // http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations
        
        List<Sleep> all = sleepRepository.findAllByUserId(forUserId);
        
        Order groupOrder = pageable.getSort().getOrderFor("groupEnding");
        Comparator<SleepStatistics> sortByTime = groupOrder.isAscending() ? BY_TIME_ASCENDING : BY_TIME_DESCENDING;
        
        SleepStatisticsCalculator groupings = new SleepStatisticsCalculator();
        List<SleepAveragesResource> allStats = groupings.calculateAveragesByGroup(all, averages, sortByTime);
        int pageNum  = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int pageStartIndex = pageNum * pageSize;
        int pageEndIndex = (pageNum + 1) * pageSize;
        List<SleepAveragesResource> pageStats = allStats.subList(pageStartIndex, Math.min(pageEndIndex, allStats.size()));
        Page<SleepAveragesResource> page = new PageImpl<>(pageStats, pageable, allStats.size());
        
        return page;
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
