package com.seebie.server.test.data;

import net.datafaker.Faker;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import static java.util.UUID.randomUUID;

public class TestData {

    private static Random random = new Random();
    private static Faker faker = new Faker(Locale.US, random);

    public static PersonalInfo randomPersonalInfo() {

        return new PersonalInfo(
                faker.internet().emailAddress(),
                faker.name().name(),
                new HashSet<>());
    }

    public static RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = randomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

}
