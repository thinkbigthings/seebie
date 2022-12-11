package com.seebie.perf;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;
import com.seebie.dto.AddressRecord;
import com.seebie.dto.PersonalInfo;
import com.seebie.dto.RegistrationRequest;
import com.seebie.dto.User;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.IntStream.range;

@Component
public class LoadTester {

    private Duration duration;
    private int numThreads;
    private boolean insertOnly;
    private String baseUrl;

    private URI registration;
    private URI users;
    private URI info;
    private URI health;

    private Random random = new Random();
    private Faker faker = new Faker(Locale.US, new Random());
    private ApiClientStateful adminClient;

    private final Instant end;

    public LoadTester(AppProperties config) {

        baseUrl = config.host();

        registration = URI.create(baseUrl + "/registration");
        users = URI.create(baseUrl + "/user");
        info = URI.create(baseUrl + "/actuator/info");
        health = URI.create(baseUrl + "/actuator/health");

        duration = config.testDuration();
        insertOnly = config.insertOnly();
        numThreads = config.threads();

        adminClient = new ApiClientStateful(baseUrl, "admin", "admin");

        System.out.println("Number Threads: " + numThreads);
        System.out.println("Insert only: " + insertOnly);

//        String hms = String.format("%d:%02d:%02d",
//                duration.toHoursPart(),
//                duration.toMinutesPart(),
//                duration.toSecondsPart());
//
//        System.out.println("Running test for " + hms + " (hh:mm:ss) connecting to " + baseUrl);

        end = Instant.now().plus(duration);
    }

    public static CompletableFuture<?> allOf(List<CompletableFuture<Void>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));
    }

    public void run() {

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(numThreads);

        try {

            List<CompletableFuture<Void>> futures = IntStream.range(0, numThreads)
                    .mapToObj(i -> runAsync(() -> makeCalls(i), executor))
                    .toList();

            allOf(futures).join();
        }
        catch(CompletionException e) {
            e.printStackTrace();
        }

        // this needs to be called or the program won't terminate
        executor.shutdown();
    }

    private boolean isDurationActive() {
        return Instant.now().isBefore(end);
    }

    private void makeCalls(int threadNumber) {

        // try to make it so not every thread is registering or logging in or out at the same time
        // the threads have a tendency to "bunch up" and all register (require password hash) at the same time
        // which is CPU intensive and tends to be a bottleneck because they are lengthy calls
        // Duration randomPause = Duration.ofMillis(random.nextInt(500));
        sleep(Duration.ofMillis(threadNumber * 200));

        try {
            while(isDurationActive()) {
                if(insertOnly) {
                    doInserts();
                }
                else {
                    doCRUD();
                }
            }
        }
        catch(Exception e) {
            throw new CompletionException(e);
        }
    }

    private void doInserts() {
        range(0, 100).forEach(i -> adminClient.post(registration, createRandomUserRegistration()));
    }

    private void doCRUD() {

        adminClient.get(URI.create(users + "/" + "admin"), User.class);

        RegistrationRequest registrationRequest = createRandomUserRegistration();
        String username = registrationRequest.username();
        String password = registrationRequest.plainTextPassword();

        URI userUrl = URI.create(users + "/" + username);
        URI updatePasswordUrl = URI.create(userUrl + "/password/update");
        URI infoUrl = URI.create(userUrl + "/personalInfo");

        System.out.println("registering and logging in " + username);
        adminClient.post(registration, registrationRequest);
        ApiClientStateful newClient = new ApiClientStateful(baseUrl, username, password);

        String newPassword = "password";
        newClient.post(updatePasswordUrl, newPassword);

        // do lots of regular work that's not logging in or updating password (those are auth / cpu heavy)
        int numLoops = 100;
        for(int i=0; i < numLoops; i++) {

            newClient.get(userUrl, User.class);

            var updatedInfo = randomPersonalInfo();
            newClient.put(infoUrl, updatedInfo);

            PersonalInfo retrievedInfo = newClient.get(userUrl, User.class).personalInfo();

            if (!retrievedInfo.equals(updatedInfo)) {
                String message = "user updates were not all persisted: " + retrievedInfo + " vs " + updatedInfo;
                throw new RuntimeException(message);
            }
        }

        newClient.logout();
        try {
            newClient.get(userUrl, User.class);
        }
        catch(Exception e) {
            System.out.println("user was appropriately logged out");
        }

        adminClient.get(info);

        adminClient.get(health);

        String page = adminClient.get(users);
    }


    private PersonalInfo randomPersonalInfo() {

        return new PersonalInfo(
                faker.internet().emailAddress(),
                faker.name().name(),
                Set.of(randomAddressRecord()));
    }

    private RegistrationRequest createRandomUserRegistration() {

        String username = "user-" + randomUUID();
        String password = "password";
        PersonalInfo info = randomPersonalInfo();

        return new RegistrationRequest(username, password, info.email());
    }

    private AddressRecord randomAddressRecord() {

        Address fakerAddress = faker.address();
        return new AddressRecord(fakerAddress.streetAddress(),
                fakerAddress.city(),
                fakerAddress.state(),
                fakerAddress.zipCode());
    }

    private void sleep(Duration sleepDuration) {
        if(sleepDuration.isZero()) {
            return;
        }
        try {
            Thread.sleep(sleepDuration.toMillis());
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
