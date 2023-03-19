package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;

import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

public class UserMapper implements Function<User, com.seebie.server.dto.User> {

    @Override
    public com.seebie.server.dto.User apply(User user) {

        return new com.seebie.server.dto.User( user.getUsername(),
                user.getRegistrationTime().toString(),
                user.getRoles().stream().map(Role::name).collect(toSet()),
                new PersonalInfo(user.getEmail(), user.getDisplayName(), user.isNotificationsEnabled()),
                ! user.getSessions().isEmpty());
    }

}
