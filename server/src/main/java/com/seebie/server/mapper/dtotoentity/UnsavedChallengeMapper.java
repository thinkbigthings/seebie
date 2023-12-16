package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.Challenge;
import com.seebie.server.entity.User;

import java.util.function.BiFunction;

public class UnsavedChallengeMapper implements BiFunction<User, Challenge, com.seebie.server.entity.Challenge> {

    @Override
    public com.seebie.server.entity.Challenge apply(User persistedUser, Challenge dto) {
        return new com.seebie.server.entity.Challenge(dto.name(), dto.description(), dto.start(), dto.finish(),
                persistedUser);
    }
}
