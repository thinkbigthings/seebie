package com.seebie.server.service;

import com.seebie.server.controller.ImportExportController;
import com.seebie.server.mapper.dtotoentity.UnsavedSleepListMapper;
import com.seebie.server.repository.SleepRepository;
import com.seebie.server.test.IntegrationTest;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.seebie.server.test.data.TestData.randomUserData;
import static org.junit.jupiter.api.Assertions.*;

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
        userService.saveNewUser(registration);
        String user1 = userService.getUserByEmail(registration.email()).publicId();

        registration = TestData.createRandomUserRegistration();
        userService.saveNewUser(registration);
        String user2 = userService.getUserByEmail(registration.email()).publicId();

        var userData = randomUserData();
        long importedSleepNum = importExportService.saveUserData(user1, userData);

        var userData1 = importExportService.retrieveUserData(user1);

        importExportService.saveUserData(user2, userData1);
        var userData2 = importExportService.retrieveUserData(user2);

        // after an export, import, and re-export: the two exports should be identical
        assertEquals(userData1, userData2);
        assertFalse(userData1.sleepData().isEmpty());
        assertFalse(userData1.challengeData().isEmpty());
        assertEquals(importedSleepNum, userData1.sleepData().size());
    }

}
