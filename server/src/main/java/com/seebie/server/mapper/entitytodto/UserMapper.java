package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.AddressRecord;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.entity.Address;
import com.seebie.server.entity.Role;
import com.seebie.server.entity.User;

import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

public class UserMapper implements Function<User, com.seebie.server.dto.User> {

    @Override
    public com.seebie.server.dto.User apply(User user) {

        return new com.seebie.server.dto.User( user.getUsername(),
                user.getRegistrationTime().toString(),
                user.getRoles().stream()
                        .map(Role::name)
                        .collect(toSet()),
                toPersonalInfoRecord(user),
                user.getSessions().size() > 0);
    }

    public PersonalInfo toPersonalInfoRecord(User user) {

        Set<AddressRecord> addresses = user.getAddresses().stream()
                .map(this::toAddressRecord)
                .collect(toSet());

        return new PersonalInfo(user.getEmail(),
                user.getDisplayName(),
                addresses);
    }

    public AddressRecord toAddressRecord(Address address) {
        return new AddressRecord(address.getLine1(),
                address.getCity(),
                address.getState(),
                address.getZip());
    }

}
