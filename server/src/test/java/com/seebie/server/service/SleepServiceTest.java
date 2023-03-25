package com.seebie.server.service;

import com.seebie.server.mapper.dtotoentity.TagMapper;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SleepServiceTest {

    private SleepService service;

    private SleepRepository sleepRepo = Mockito.mock(SleepRepository.class);
    private TagMapper tagMapper = Mockito.mock(TagMapper.class);
    private UnsavedSleepListMapper unsavedMapper = Mockito.mock(UnsavedSleepListMapper.class);

    @BeforeEach
    public void setup() {

        service = new SleepService(sleepRepo, tagMapper, unsavedMapper);

        when(sleepRepo.findAllByUsername(ArgumentMatchers.any(String.class))).thenReturn(new ArrayList<>());
    }

    @Test
    public void testNoDataFound() {

        // should print only the header
        String csv = service.exportCsv("someuser");

        assertEquals("Time-Asleep,Time-Awake,Duration-Minutes,Notes\r\n", csv);
    }


}
