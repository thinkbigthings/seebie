package com.seebie.server.test.data;

import com.seebie.server.dto.SleepData;
import net.datafaker.Faker;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.util.UUID.randomUUID;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;

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
            session = randomDuration(session);
            session = randomNotes(session);
            newData.add(session);
        }

        return newData;
    }

    public static SleepData randomNotes(SleepData data) {
        String notes = faker.lorem().paragraph(5);
        return new SleepData(notes, data.outOfBed(), data.tags(), data.startTime(), data.stopTime());
    }

    public static SleepData decrementDays(SleepData data, long days) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().minusDays(days),
                data.stopTime().minusDays(days));
    }

    public static SleepData randomDuration(SleepData data) {
        return new SleepData(data.notes(), data.outOfBed(), data.tags(),
                data.startTime().plusMinutes(random.nextInt(60)),
                data.stopTime().minusMinutes(random.nextInt(60)));
    }

    public static class ArgumentBuilder {

        private String host;

        public ArgumentBuilder() {
            this.host = "";
        }
        public ArgumentBuilder(String host) {
            this.host = host;
        }

        public Arguments post(String urlPath, Object reqBody, int expected) {
            return Arguments.of(new AppRequest().method(POST).url(host + urlPath).body(reqBody), expected);
        }

        public Arguments put(String urlPath, Object reqBody, int expected) {
            return Arguments.of(new AppRequest().method(PUT).url(host + urlPath).body(reqBody), expected);
        }

        public Arguments get(String urlPath, String[] requestParams, int expected) {
            return Arguments.of(new AppRequest().method(GET).url(host + urlPath).params(requestParams), expected);
        }

        public Arguments get(String urlPath, int expected) {
            return get(urlPath, new String[]{}, expected);
        }

        public Arguments delete(String urlPath, int expected) {
            return Arguments.of(new AppRequest().method(DELETE).url(host + urlPath), expected);
        }
    }
}
