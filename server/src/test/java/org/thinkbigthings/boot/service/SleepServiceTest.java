package org.thinkbigthings.boot.service;




import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.thinkbigthings.boot.dto.SleepResource.DATE_TIME_FORMAT;
import static org.thinkbigthings.boot.web.SleepResourceController.DEFAULT_AVG_SORT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.domain.User;
import org.thinkbigthings.boot.dto.SleepAveragesResource;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.boot.repository.SleepSessionRepository;
import org.thinkbigthings.boot.repository.UserRepository;
import org.thinkbigthings.sleep.SleepStatisticsCalculator.Group;

public class SleepServiceTest {

    private SleepService service;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final SleepSessionRepository sleepRepo = mock(SleepSessionRepository.class);
    private final User user0 = new User();
    

    @Before
    public void setup() throws Exception {
        service = new SleepService(sleepRepo, userRepo);

        user0.setId(15L);
        
        List<Sleep> allSleepRecords = new ArrayList<>();
        DateTime start = DATE_TIME_FORMAT.parseDateTime("2012-06-04 05:30 AM EST");
        DateTime finish = DATE_TIME_FORMAT.parseDateTime("2015-01-05 05:30 AM EST");
        for (DateTime time = start; time.isBefore(finish); time = time.plusDays(1)  ) {
            String curStart = DATE_TIME_FORMAT.print(time.minusMinutes(randInt(360, 510)));
            String curFinish = DATE_TIME_FORMAT.print(time);
            SleepResource newResource = new SleepResource(curStart, curFinish, 0, randInt(5, 30),  randInt(0, 1)*20);
            allSleepRecords.add(new Sleep(user0, newResource));
        }
        when(sleepRepo.findAllByUserId(user0.getId())).thenReturn(allSleepRecords);
    }

    @Test
    public void testSmallPage() throws Exception {
        Pageable pageable = new PageRequest(0, 20, DEFAULT_AVG_SORT);
        Page<SleepAveragesResource> page = service.getSleepAverages(user0.getId(), pageable, Group.YEAR);
        
        assertEquals(4, page.getNumberOfElements());
    }
    

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    private static Random rand = new Random(); 
    private static int randInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

}
