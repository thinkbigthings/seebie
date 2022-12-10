package org.thinkbigthings.zdd.server.mapper.entitytodto;

import org.thinkbigthings.zdd.dto.AddressRecord;
import org.thinkbigthings.zdd.dto.PersonalInfo;
import org.thinkbigthings.zdd.server.entity.Address;
import org.thinkbigthings.zdd.server.entity.Role;
import org.thinkbigthings.zdd.server.entity.User;

import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toSet;

public class UserMapper implements Function<User, org.thinkbigthings.zdd.dto.User> {

    @Override
    public org.thinkbigthings.zdd.dto.User apply(User user) {

        return new org.thinkbigthings.zdd.dto.User( user.getUsername(),
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
