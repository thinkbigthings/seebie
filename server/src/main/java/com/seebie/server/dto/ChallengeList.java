package com.seebie.server.dto;

import java.util.List;

public record ChallengeList(List<ChallengeDetails> current, List<ChallengeDetails> completed, List<ChallengeDetails> upcoming) {

}
