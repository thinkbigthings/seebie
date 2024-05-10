package com.seebie.server.service;

import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.repository.ChallengeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ChallengeServiceTest {

    private ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private UnsavedChallengeListMapper challengeListMapper = Mockito.mock(UnsavedChallengeListMapper.class);

    private ChallengeService service;

    @BeforeEach
    public void setup() {
        service = new ChallengeService(challengeRepository, challengeListMapper);

        when(challengeRepository.findByUsername(anyString(), anyLong())).thenReturn(Optional.empty());
    }

    @Test
    public void testRemoveMissingChallenge() {
        assertThrows(EntityNotFoundException.class, () -> service.remove("username", 1L));
    }

}
