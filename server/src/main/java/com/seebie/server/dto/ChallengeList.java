package com.seebie.server.dto;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public record ChallengeList(List<ChallengeDetailDto> current, List<ChallengeDetailDto> completed, List<ChallengeDetailDto> upcoming) {


    public static ChallengeList newChallengeList(List<ChallengeDetailDto> challenges, LocalDate today) {

        var groupedChallenges = challenges.stream().collect(groupingBy(c -> categorize(c.challenge(), today)));

        List<ChallengeDetailDto> completed = groupedChallenges.getOrDefault(ChallengeCategory.COMPLETED, List.of());
        List<ChallengeDetailDto> upcoming = groupedChallenges.getOrDefault(ChallengeCategory.UPCOMING, List.of());
        List<ChallengeDetailDto> current = groupedChallenges.getOrDefault(ChallengeCategory.CURRENT, List.of());

        return new ChallengeList(current, completed, upcoming);
    }

    private enum ChallengeCategory {
        COMPLETED, UPCOMING, CURRENT;
    }

    private static ChallengeCategory categorize(ChallengeDto challenge, LocalDate today) {
        if (challenge.finish().isBefore(today)) {
            return ChallengeCategory.COMPLETED;
        }
        if (challenge.start().isAfter(today)) {
            return ChallengeCategory.UPCOMING;
        }
        return ChallengeCategory.CURRENT;
    }
}
