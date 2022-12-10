package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.thinkbigthings.zdd.dto.*;
import org.thinkbigthings.zdd.server.entity.StoreItem_;
import org.thinkbigthings.zdd.server.mapper.entitytodto.OperatorToStringMapper;
import org.thinkbigthings.zdd.server.service.StoreService;
import org.thinkbigthings.zdd.server.service.UserService;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;
import org.thinkbigthings.zdd.server.test.client.ParsablePage;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thinkbigthings.zdd.server.entity.Operator.EQ;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;

public class SearchConfigIntegrationTest extends IntegrationTest {

    protected static Logger LOG = LoggerFactory.getLogger(SearchConfigIntegrationTest.class);

    private ObjectMapper mapper = new ObjectMapper();
    private OperatorToStringMapper opMapper = new OperatorToStringMapper();

    private static String baseUrl;
    private static URI users;

    private static String storeName;
    private static String testUserName;
    private static String testUserPassword;
    private static URI testUserUrl;
    private static URI testUserSearchConfigUrl;
    private static URI storeUrl;

    private static ApiClientStateful userClient;

    @BeforeAll
    public static void createTestData(@Autowired UserService userService, @Autowired StoreService storeService,
                                      @LocalServerPort int randomServerPort) throws IOException  {

        LOG.info("");
        LOG.info("=======================================================================================");
        LOG.info("Creating test data");
        LOG.info("");

        storeName = "store-" + UUID.randomUUID();
        storeService.saveNewStore(new StoreRecord(storeName, storeName));
        storeService.updateStoreItems(storeName, readItems("keystone-devon-flower-20210909.json"));

        baseUrl = "https://localhost:" + randomServerPort + "/";
        users = URI.create(baseUrl + "user");
        storeUrl = URI.create(baseUrl + "store");

        RegistrationRequest testUserRegistration = createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);

        testUserName = testUserRegistration.username();
        testUserPassword = testUserRegistration.plainTextPassword();
        testUserUrl = URI.create(users + "/" + testUserName);
        testUserSearchConfigUrl = URI.create(testUserUrl + "/searchconfig");

        userClient = new ApiClientStateful(baseUrl, testUserName, testUserPassword);
    }

    @Test
    @DisplayName("Update search config")
    public void testUpdateSearchConfig() throws JsonProcessingException {

        String results = userClient.get(storeUrl);
        Page<StoreRecord> page = mapper.readValue(results, new TypeReference<ParsablePage<StoreRecord>>() {});
        List<String> storeNames = page.stream().map(StoreRecord::name).toList();

        SearchItemsInStores searches = userClient.get(testUserSearchConfigUrl, SearchItemsInStores.class);
        SearchItemsInStores updateSearchesRequest = searches.withStore(storeNames.get(0))
                .withSearch(StoreItem_.STRAIN, opMapper.apply(EQ), "Hash Haze");

        userClient.put(testUserSearchConfigUrl, updateSearchesRequest);

        SearchItemsInStores updatedSearches = userClient.get(testUserSearchConfigUrl, SearchItemsInStores.class);
        assertEquals(updateSearchesRequest, updatedSearches);
    }


}
