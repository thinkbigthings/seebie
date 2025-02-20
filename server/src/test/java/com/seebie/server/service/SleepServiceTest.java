package com.seebie.server.service;

import com.seebie.server.dto.SleepData;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SleepServiceTest {

    private SleepRepository sleepRepository = Mockito.mock(SleepRepository.class);
    private UnsavedSleepListMapper entityMapper = Mockito.mock(UnsavedSleepListMapper.class);
    private SleepService service;
    private SleepData data = createRandomSleepData();

    @BeforeEach
    public void setup() {
        service = new SleepService(sleepRepository, entityMapper);

        when(sleepRepository.findBy(any(UUID.class), anyLong())).thenReturn(Optional.empty());
    }

    @Test
    public void testNotFound() {
        assertAll(
                "Data not found should throw exceptions",
                () -> assertThrows(ResponseStatusException.class, () -> service.remove(randomUUID(), 1L)),
                () -> assertThrows(ResponseStatusException.class, () -> service.update(randomUUID(), 1L, data)),
                () -> assertThrows(ResponseStatusException.class, () -> service.retrieve(randomUUID(), 1L))
        );
    }

}
