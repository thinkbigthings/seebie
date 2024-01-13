package com.seebie.server.test.data;

import com.seebie.server.dto.*;
import com.seebie.server.mapper.entitytodto.SleepDetailsToCsvRow;
import net.datafaker.Faker;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv.headerRow;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;

public class TestData {

    private static Random random = new Random();
    private static Faker faker = new Faker(Locale.US, random);

    public static PersonalInfo createRandomPersonalInfo() {
        return new PersonalInfo(faker.internet().emailAddress(), faker.name().name());
    }

    public static RegistrationRequest createRandomUserRegistration(String usernamePrefix) {

        String username = usernamePrefix + "-" + randomUUID();
        String password = "password";
        PersonalInfo info = createRandomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    public static RegistrationRequest createRandomUserRegistration() {
        return createRandomUserRegistration("user");
    }

    /**
     *
     * @param daysOffsetStart if negative, number of days to go back in time from today.
     * @return
     */
    public static Challenge createRandomChallenge(int daysOffsetStart, int lengthDays) {

        var start = LocalDate.now().plusDays(daysOffsetStart);
        var finish = start.plusDays(lengthDays);

        return new Challenge(STR."\{faker.starTrek().location()} \{randomUUID()}",
                faker.lorem().paragraph(3),
                start, finish);
    }

    public static MockMultipartFile createMultipart(String content) {
        return new MockMultipartFile("file","export.csv", TEXT_PLAIN_VALUE, content.getBytes());
    }

    public static String createCsv(int listCount) {
        return createCsv(listCount, AMERICA_NEW_YORK);
    }

    public static SleepDetails toSleepDetails(SleepData data) {
        int minutesAsleep = (int) Duration.between(data.startTime(), data.stopTime()).toMinutes();
        return new SleepDetails(0L, minutesAsleep, data);
    }

    public static String createCsv(int listCount, String zoneId) {

        var data = createRandomSleepData(listCount, zoneId);

        SleepDetailsToCsvRow toCsv = new SleepDetailsToCsvRow();
        StringBuilder csvString = new StringBuilder();

        String headerRow = headerRow() + "\r\n";

        String body = data.stream()
                .map(TestData::toSleepDetails)
                .map(toCsv)
                .map(row -> String.join(",", row))
                .collect(joining("\r\n"));

        csvString.append(headerRow);
        csvString.append(body);

        return csvString.toString();
    }

    /**
     *
     * @return A list of daily SleepData whose zeroth element is today and last element is listCount days ago.
     */
    public static List<SleepData> createRandomSleepData(int listCount, String zoneId) {

        var stopTime = ZonedDateTime.now();
        var newData = new ArrayList<SleepData>();
        SleepData current;

        for(int i=0; i < listCount; i++) {
            current = createRandomSleepData(stopTime, zoneId);
            newData.add(current);
            stopTime = stopTime.minusDays(1);
        }

        return newData;
    }

    public static SleepData createStandardSleepData(ZonedDateTime startTime, ZonedDateTime stopTime) {
        return new SleepData("", 0, startTime, stopTime, AMERICA_NEW_YORK);
    }

    public static SleepData createRandomSleepData() {
        return createRandomSleepData(ZonedDateTime.now(), AMERICA_NEW_YORK);
    }

    public static int getGaussianRandom(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        int mid = (max + min) / 2;
        double sd = (max - min) / 6.0;  // Standard Deviation, 99.7% values lies within mean ± 3*SD for normal distribution
        double result;

        do {
            result = (random.nextGaussian() * sd) + mid;
        } while (result < min || result > max); // repeat until a valid number in range is generated

        return (int) Math.round(result);
    }

    public static SleepData createRandomSleepData(ZonedDateTime stopTime, String zoneId) {
        long sleepDuration = getGaussianRandom(3*60, 9*60);
        var startTime = stopTime.withZoneSameInstant(ZoneId.of(zoneId)).minusMinutes(sleepDuration);
        return new SleepData(faker.lorem().paragraph(3), 0,
                startTime.plusMinutes(random.nextInt(60)),
                stopTime.minusMinutes(random.nextInt(60)),
                zoneId);
    }

    public static SleepData increment(SleepData data, Duration amountToAdd) {
        return new SleepData(data.notes(), data.minutesAwake(),
                data.startTime().plus(amountToAdd),
                data.stopTime().plus(amountToAdd), data.zoneId());
    }

    public static class RequestResponseBuilder {

        private Map<Request, Response> map = new HashMap<>();

        public List<Arguments> build(Response.Role role) {
            return map.entrySet().stream()
                            .map(entry -> Arguments.of(entry.getKey(), entry.getValue().expected(role)))
                            .toList();
        }

        public void post(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
            map.put(new Request().method(POST).url(urlPath).body(reqBody), new Response(unauthenticated, user, admin));
        }

        public void put(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
            map.put(new Request().method(PUT).url(urlPath).body(reqBody), new Response(unauthenticated, user, admin));
        }

        public void get(String urlPath, String[] requestParams,int unauthenticated, int user, int admin) {
            map.put(new Request().method(GET).url(urlPath).params(requestParams), new Response(unauthenticated, user, admin));
        }

        public void get(String urlPath, int unauthenticated, int user, int admin) {
            get(urlPath, new String[]{}, unauthenticated, user, admin);
        }

        public void delete(String urlPath, int unauthenticated, int user, int admin) {
            map.put(new Request().method(DELETE).url(urlPath), new Response(unauthenticated, user, admin));
        }
    }

    public static class ArgumentBuilder {

        private String host;

        public ArgumentBuilder() {
            this.host = "";
        }

        public Arguments post(String urlPath, Object reqBody, int expected) {
            return Arguments.of(new Request().method(POST).url(host + urlPath).body(reqBody), expected);
        }

        public Arguments put(String urlPath, Object reqBody, int expected) {
            return Arguments.of(new Request().method(PUT).url(host + urlPath).body(reqBody), expected);
        }

        public Arguments get(String urlPath, String[] requestParams, int expected) {
            return Arguments.of(new Request().method(GET).url(host + urlPath).params(requestParams), expected);
        }

        public Arguments get(String urlPath, int expected) {
            return get(urlPath, new String[]{}, expected);
        }

        public Arguments delete(String urlPath, int expected) {
            return Arguments.of(new Request().method(DELETE).url(host + urlPath), expected);
        }
    }
}