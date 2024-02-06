package com.seebie.server.dto;

import java.util.List;

public record UserData(List<SleepData> sleepData, List<Challenge> challengeData) {
}
