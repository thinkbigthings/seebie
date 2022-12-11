package com.seebie.server.test.data;

import net.datafaker.Address;
import net.datafaker.Faker;
import com.seebie.dto.AddressRecord;
import com.seebie.dto.PersonalInfo;
import com.seebie.dto.RegistrationRequest;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static java.util.UUID.randomUUID;

public class TestData {

    private static Random random = new Random();
    private static Faker faker = new Faker(Locale.US, random);

    public static PersonalInfo randomPersonalInfo() {

        return new PersonalInfo(
                faker.internet().emailAddress(),
                faker.name().name(),
                Set.of(randomAddressRecord()));
    }

    public static RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = randomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    public static AddressRecord randomAddressRecord() {

        Address fakerAddress = faker.address();
        return new AddressRecord(fakerAddress.streetAddress(),
                fakerAddress.city(),
                fakerAddress.state(),
                fakerAddress.zipCode());
    }
}
