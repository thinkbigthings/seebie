package com.seebie.server.service;

import com.seebie.server.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.seebie.server.test.data.TestData.randomUserData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ImportExportServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ImportExportService importExportService;

    @Test
    public void testImportExportUserData() {

        String user1 = saveNewUser().toString();
        String user2 = saveNewUser().toString();

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
