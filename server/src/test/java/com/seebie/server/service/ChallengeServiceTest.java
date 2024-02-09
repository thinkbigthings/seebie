package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDetails;
import com.seebie.server.mapper.dtotoentity.UnsavedChallengeListMapper;
import com.seebie.server.repository.ChallengeRepository;
import com.seebie.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeServiceTest {

    private UserRepository userRepo = Mockito.mock(UserRepository.class);
    private ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    private UnsavedChallengeListMapper challengeListMapper = Mockito.mock(UnsavedChallengeListMapper.class);

    private ChallengeService service;

    @BeforeEach
    public void setup() {
        service = new ChallengeService(userRepo, challengeRepository, challengeListMapper);
    }

    @Test
    public void testEmptyChallengeList() {

        LocalDate today = LocalDate.now();

        List<ChallengeDetails> challenges = List.of();

        var sortedChallenges = service.sortChallenges(challenges, today);

        assertEquals(0, sortedChallenges.current().size());
        assertEquals(0, sortedChallenges.completed().size());
        assertEquals(0, sortedChallenges.upcoming().size());
    }

    @Test
    public void testSortChallenges() {

        LocalDate today = LocalDate.now();

        var completedChallenge = new ChallengeDetails(1L, "Completed", "", today.minusDays(10), today.minusDays(5));
        var currentChallenge = new ChallengeDetails(2L, "Current", "", today.minusDays(1), today.plusDays(1));
        var upcomingChallenge = new ChallengeDetails(3L, "Upcoming", "", today.plusDays(5), today.plusDays(10));

        var challenges = List.of(completedChallenge, currentChallenge, upcomingChallenge);

        var sortedChallenges = service.sortChallenges(challenges, today);

        assertTrue(sortedChallenges.completed().contains(completedChallenge), "Completed list should contain the completed challenge.");
        assertTrue(sortedChallenges.current().contains(currentChallenge), "Current list should contain the current challenge.");
        assertTrue(sortedChallenges.upcoming().contains(upcomingChallenge), "Upcoming list should contain the upcoming challenge.");
        assertEquals(1, sortedChallenges.current().size());
        assertEquals(1, sortedChallenges.completed().size());
        assertEquals(1, sortedChallenges.upcoming().size());
    }
}
