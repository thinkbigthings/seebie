package com.seebie.server.dto;

import java.util.List;

public record ChallengeList(List<Challenge> current, List<Challenge> completed, List<Challenge> upcoming) {

}
