package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.ChallengeDetailDto;
import com.seebie.server.entity.Challenge;

import java.util.function.Function;

public class ChallengeMapper implements Function<Challenge, ChallengeDetailDto> {

    @Override
    public ChallengeDetailDto apply(Challenge entity) {
        return new ChallengeDetailDto(entity.getId(), entity.getName(), entity.getDescription(), entity.getStart(), entity.getFinish());
    }
}
