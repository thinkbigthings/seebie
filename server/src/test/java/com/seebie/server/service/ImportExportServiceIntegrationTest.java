package com.seebie.server.service;

import com.seebie.server.controller.ImportExportController;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.seebie.server.test.data.TestData.createCsv;
import static com.seebie.server.test.data.TestData.randomUserData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportExportServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ImportExportController importExportController;

    @Autowired
    private ImportExportService importExportService;

    @Autowired
    private SleepRepository sleepRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UnsavedSleepListMapper sleepListMapper;


    @Test
    public void testImportExportUserData() throws Exception {

        var registration = TestData.createRandomUserRegistration();
        String user1 = registration.username();
        userService.saveNewUser(registration);

        registration = TestData.createRandomUserRegistration();
        String user2 = registration.username();
        userService.saveNewUser(registration);

        var userData = randomUserData();
        long importedSleepNum = importExportService.saveUserData(user1, userData);

        var userData1 = importExportService.retrieveUserData(user1);

        importExportService.saveUserData(user2, userData1);
        var userData2 = importExportService.retrieveUserData(user2);

        // after an export, import, and re-export: the two exports should be identical
        assertEquals(userData1, userData2);
        assertEquals(importedSleepNum, userData1.sleepData().size());
    }


    @Test
    public void testDownloadWithTimezone() {

        var registration = TestData.createRandomUserRegistration();
        String user1 = registration.username();
        userService.saveNewUser(registration);

        registration = TestData.createRandomUserRegistration();
        String user2 = registration.username();
        userService.saveNewUser(registration);

        importExportService.saveCsv(user1, createCsv(3, AMERICA_NEW_YORK));
        var retrievedCsv1 = importExportService.retrieveCsv(user1);

        importExportService.saveCsv(user2, retrievedCsv1);
        var retrievedCsv2 = importExportService.retrieveCsv(user2);

        // after an export, import, and re-export: the two exports should be identical
        assertEquals(retrievedCsv1, retrievedCsv2);
        assertTrue(retrievedCsv1.contains(AMERICA_NEW_YORK));
    }
}
