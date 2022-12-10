package org.thinkbigthings.zdd.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.SearchItemsInStores;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.scraper.keystone.Item;
import org.thinkbigthings.zdd.server.service.StoreService;
import org.thinkbigthings.zdd.server.service.UserService;
import org.thinkbigthings.zdd.server.test.client.ApiClientStateful;
import org.thinkbigthings.zdd.server.test.data.TestData;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.join;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.thinkbigthings.zdd.server.entity.StoreItem_.WEIGHT_GRAMS;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


public class SearchIntegrationTest extends IntegrationTest {

    protected static URI base;
    protected static URI searchUrl;

    private String username;
    private String storeName;
    private URI userSearchUrl;
    private URI userSearchConfigUrl;
    private ApiClientStateful userClient;

    @BeforeAll
    public static void createReadOnlyTestData(@LocalServerPort int randomServerPort) {

        base = URI.create("https://localhost:" + randomServerPort + "/");
        searchUrl = base.resolve("search");
    }

    @BeforeEach
    public void createTestUser(@Autowired StoreService storeService, @Autowired UserService userService) throws IOException {

        storeName = "store-" + UUID.randomUUID();
        storeService.saveNewStore(new StoreRecord(storeName, storeName));
        storeService.updateStoreItems(storeName, readItems("keystone-devon-flower-20210909.json"));

        // user created between item updates so a search picks up only updated items.
        RegistrationRequest testUserRegistration = createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        username = testUserRegistration.username();
        String testUserPassword = testUserRegistration.plainTextPassword();
        userClient = new ApiClientStateful(base, username, testUserPassword);
        userSearchUrl = base.resolve(join("/", "user", username, "search"));
        userSearchConfigUrl = base.resolve(join("/", "user", username, "searchconfig"));

        // update again, then last scan will grab items changed since the first load
        storeService.updateStoreItems(storeName, readItems("keystone-devon-flower-20210910.json"));
    }

    @Test
    @DisplayName("Test ad hoc queries on web api")
    public void testAdHocSearchApi() throws JsonProcessingException {

        var userSearch = TestData.readSavedSearch("saved-search-cherry-diesel-or-high-thc.json");
        userSearch = userSearch.withStore(storeName);

        String responseBody = userClient.post(searchUrl, userSearch).body();
        ObjectMapper mapper = new ObjectMapper();
        var items = mapper.readValue(responseBody, new TypeReference<List<Item>>() {});

        assertEquals(77, items.size());
    }

    @Test
    @DisplayName("Test web api using saved searches")
    public void testSavedSearchApi() throws JsonProcessingException {

        var searchByStore = SearchItemsInStores.newSavedSearches().withStore(storeName);
        var updateSearch = searchByStore.withSearch(WEIGHT_GRAMS, ">=", "0");
        userClient.put(userSearchConfigUrl, updateSearch);

        String responseBody = userClient.get(userSearchUrl);
        ObjectMapper mapper = new ObjectMapper();
        var items = mapper.readValue(responseBody, new TypeReference<List<Item>>() {});

        // find all updated with an always true search
        assertEquals(6, items.size());
    }

}
