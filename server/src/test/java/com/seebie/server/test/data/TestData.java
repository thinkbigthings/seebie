package com.seebie.server.test.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.dto.*;
import com.seebie.server.mapper.entitytodto.SleepDetailsToCsvRow;
import net.datafaker.Faker;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

import static com.seebie.server.Functional.uncheck;
import static com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv.headerRow;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;
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

    public static List<Challenge> createRandomChallenges() {

        var current = createRandomChallenge(-1, 14);
        var completed = List.of(createRandomChallenge(-16, 14),
                createRandomChallenge(-31, 14),
                createRandomChallenge(-46, 14));
        var upcoming = List.of(createRandomChallenge(15, 14),
                createRandomChallenge(30, 14));

        var dtos = new ArrayList<Challenge>();
        dtos.addAll(completed);
        dtos.addAll(upcoming);
        dtos.add(current);
        return dtos;
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

    public static UserData randomUserData() {
        return new UserData(createRandomSleepData(1, AMERICA_NEW_YORK), createRandomChallenges());
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

    /**
     * This class assembles all the information necessary for an HTTP request and bundles it with an expected http
     * response code so that they can be used in a parameterized WebMvcTest
     */
    public static class ArgumentsBuilder {

        private Function<Object, String> bodyMapper;

        public ArgumentsBuilder(ObjectMapper mapper) {
            // If the test data is a string, presume it is already in the correct format and return directly.
            // Because if you pass a string "" to the object mapper, it doesn't return the string, it returns """".
            this.bodyMapper = uncheck( obj -> obj instanceof String testData
                    ? testData
                    : mapper.writerFor(obj.getClass()).writeValueAsString(obj)
            );
        }

        public RequestBuilder toMvcRequest(HttpMethod method, String urlPath, Object reqBody) {
            return toMvcRequest(method, urlPath, reqBody, new String[0]);
        }

        public RequestBuilder toMvcRequest(HttpMethod method, String urlPath, Object reqBody, String[] reqParams) {
            var params = toParams(reqParams);
            var builder = reqBody instanceof MockMultipartFile multipartFile
                    ? multipart(urlPath).file(multipartFile)
                    : request(method, urlPath).content(bodyMapper.apply(reqBody)).params(params).contentType(APPLICATION_JSON);
            return builder.secure(true);
        }

        public Arguments args(HttpMethod method, String urlPath, Object reqBody, int expectedResponse) {
            return args(method, urlPath, reqBody, new String[0], expectedResponse);
        }

        public Arguments args(HttpMethod method, String urlPath, Object reqBody, String[] reqParams, int expectedResponse) {
            var request = toMvcRequest(method, urlPath, reqBody, reqParams);
            return Arguments.of(request, expectedResponse);
        }

        /**
         * Works the same as HttpRequest.headers()
         * Do not need to url encode the parameters.
         *
         * @param newReqParams
         * @return
         */
        public static MultiValueMap<String, String> toParams(String... newReqParams) {
            if(newReqParams.length % 2 != 0) {
                throw new IllegalArgumentException("Number of args must be even");
            }
            var newParams = new LinkedMultiValueMap<String, String>();
            for (int i = 0; i < newReqParams.length; i += 2) {
                newParams.add(newReqParams[i], newReqParams[i + 1]);
            }
            return unmodifiableMultiValueMap(newParams);
        }
    }


    /**
     * This class is a convenience for creating a set of arguments for a set of roles.
     * It is used to create a list of arguments for each role, so that they can be used in a parameterized WebMvcTest
     * The advantage of this is that it allows you to test the same endpoint with different roles in the same test
     * and ensures that the same tests are run for each role.
     */
    public static class RoleArgumentsBuilder {

        public enum Role {
            USER, ADMIN, UNAUTHENTICATED
        }

        private List<Arguments> unauthenticated = new ArrayList<>();
        private List<Arguments> user = new ArrayList<>();
        private List<Arguments> admin = new ArrayList<>();

        private ArgumentsBuilder builder;

        public RoleArgumentsBuilder(ObjectMapper mapper) {
            this.builder = new ArgumentsBuilder(mapper);
        }

        public List<Arguments> getArguments(Role role) {
            return switch(role) {
                case USER -> this.user;
                case UNAUTHENTICATED -> this.unauthenticated;
                case ADMIN -> this.admin;
            };
        }

        // set of convenience methods to account for all roles at once

        public void post(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
            addArgs(builder.toMvcRequest(POST, urlPath, reqBody), unauthenticated, user, admin);
        }

        public void put(String urlPath, Object reqBody, int unauthenticated, int user, int admin) {
            addArgs(builder.toMvcRequest(HttpMethod.PUT, urlPath, reqBody), unauthenticated, user, admin);
        }

        public void get(String urlPath, String[] requestParams, int unauthenticated, int user, int admin) {
            addArgs(builder.toMvcRequest(GET, urlPath, "", requestParams), unauthenticated, user, admin);
        }

        public void get(String urlPath, int unauthenticated, int user, int admin) {
            addArgs(builder.toMvcRequest(GET, urlPath, ""), unauthenticated, user, admin);
        }

        public void delete(String urlPath, int unauthenticated, int user, int admin) {
            addArgs(builder.toMvcRequest(HttpMethod.DELETE, urlPath, ""), unauthenticated, user, admin);
        }

        private void addArgs(RequestBuilder request, int unauthenticated, int user, int admin) {
            this.unauthenticated.add(Arguments.of(request, unauthenticated));
            this.user.add(Arguments.of(request, user));
            this.admin.add(Arguments.of(request, admin));
        }
    }

}