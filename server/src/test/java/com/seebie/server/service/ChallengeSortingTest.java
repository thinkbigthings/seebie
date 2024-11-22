package com.seebie.server.service;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.dto.ChallengeList;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeSortingTest {

    @Test
    public void testEmptyChallengeList() {

        LocalDate today = LocalDate.now();

        List<ChallengeDetailDto> challenges = List.of();

        var sortedChallenges = ChallengeList.newChallengeList(challenges, today);

        assertEquals(0, sortedChallenges.current().size());
        assertEquals(0, sortedChallenges.completed().size());
        assertEquals(0, sortedChallenges.upcoming().size());
    }

    @Test
    public void testSortChallenges() {

        LocalDate today = LocalDate.now();

        var completedChallenge = new ChallengeDetailDto(1L, "Completed", "", today.minusDays(10), today.minusDays(5));
        var currentChallenge = new ChallengeDetailDto(2L, "Current", "", today.minusDays(1), today.plusDays(1));
        var upcomingChallenge = new ChallengeDetailDto(3L, "Upcoming", "", today.plusDays(5), today.plusDays(10));

        var challenges = List.of(completedChallenge, currentChallenge, upcomingChallenge);

        var sortedChallenges = ChallengeList.newChallengeList(challenges, today);

        assertTrue(sortedChallenges.completed().contains(completedChallenge), "Completed list should contain the completed challenge.");
        assertTrue(sortedChallenges.current().contains(currentChallenge), "Current list should contain the current challenge.");
        assertTrue(sortedChallenges.upcoming().contains(upcomingChallenge), "Upcoming list should contain the upcoming challenge.");
        assertEquals(1, sortedChallenges.current().size());
        assertEquals(1, sortedChallenges.completed().size());
        assertEquals(1, sortedChallenges.upcoming().size());
    }
}
