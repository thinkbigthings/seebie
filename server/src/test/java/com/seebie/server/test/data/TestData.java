package com.seebie.server.test.data;

import com.seebie.server.dto.SleepData;
import net.datafaker.Faker;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.util.UUID.randomUUID;

public class TestData {

    private static Random random = new Random();
    private static Faker faker = new Faker(Locale.US, random);

    public static PersonalInfo createRandomPersonalInfo() {
        return new PersonalInfo(faker.internet().emailAddress(), faker.name().name());
    }

    public static RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = createRandomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    /**
     *
     * @return A list whose zeroth element is today and last element is .length() days ago.
     */
    public static List<SleepData> createSleepData(int listCount) {

        SleepData today = new SleepData();

        List<SleepData> newData = new ArrayList<>();
        for(int i=0; i < listCount; i++) {
            SleepData session = decrementDays(today, i);
            session = randomizeDuration(session);
            newData.add(session);
        }

        return newData;
    }

    private static SleepData decrementDays(SleepData data, long days) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().minusDays(days),
                data.stopTime().minusDays(days));
    }

    private static SleepData randomizeDuration(SleepData data) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().plusMinutes(random.nextInt(60)),
                data.stopTime().minusMinutes(random.nextInt(60)));
    }
}
