package com.seebie.server.dto;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public record ChallengeList(List<ChallengeDetails> current, List<ChallengeDetails> completed, List<ChallengeDetails> upcoming) {


    public static ChallengeList newChallengeList(List<ChallengeDetails> challenges, LocalDate today) {

        var groupedChallenges = challenges.stream().collect(groupingBy(c -> categorize(c.challenge(), today)));

        List<ChallengeDetails> completed = groupedChallenges.getOrDefault(ChallengeCategory.COMPLETED, List.of());
        List<ChallengeDetails> upcoming = groupedChallenges.getOrDefault(ChallengeCategory.UPCOMING, List.of());
        List<ChallengeDetails> current = groupedChallenges.getOrDefault(ChallengeCategory.CURRENT, List.of());

        return new ChallengeList(current, completed, upcoming);
    }

    private enum ChallengeCategory {
        COMPLETED, UPCOMING, CURRENT;
    }

    private static ChallengeCategory categorize(Challenge challenge, LocalDate today) {
        if (challenge.finish().isBefore(today)) {
            return ChallengeCategory.COMPLETED;
        }
        if (challenge.start().isAfter(today)) {
            return ChallengeCategory.UPCOMING;
        }
        return ChallengeCategory.CURRENT;
    }
}
