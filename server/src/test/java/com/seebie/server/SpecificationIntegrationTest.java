package com.seebie.server;

import com.seebie.server.entity.StoreItem_;
import com.seebie.server.entity.Subspecies;
import com.seebie.server.service.SearchConfigService;
import com.seebie.server.service.SearchService;
import com.seebie.server.service.StoreService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.seebie.dto.RegistrationRequest;
import com.seebie.dto.ItemSearch;
import com.seebie.dto.SearchItemsInStores;
import com.seebie.dto.StoreRecord;
import org.thinkbigthings.zdd.server.service.*;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static com.seebie.server.test.data.TestData.readItems;


public class SpecificationIntegrationTest extends IntegrationTest {

    @Autowired
    private SearchConfigService configService;

    @Autowired
    private SearchService searchService;

    private String username;
    private String storeName;

    @BeforeEach
    public void createTestUser(@Autowired StoreService storeService, @Autowired UserService userService) throws IOException {

        storeName = "store-" + UUID.randomUUID();

        storeService.saveNewStore(new StoreRecord(storeName, storeName));
        storeService.updateStoreItems(storeName, TestData.readItems("keystone-devon-flower-20210909.json"));

        RegistrationRequest testUserRegistration = TestData.createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        username = testUserRegistration.username();

        // update again, then last scan will grab items changed since the first load
        storeService.updateStoreItems(storeName, TestData.readItems("keystone-devon-flower-20210910.json"));
    }

    @Test
    @DisplayName("Test Specification queries from dto request")
    public void testAdHocSearch() {

        var userSearch = TestData.readSavedSearch("saved-search-cherry-diesel-or-high-thc.json");
        userSearch = userSearch.withStore(storeName);

        var items = searchService.search(userSearch);
        assertEquals(77, items.size());
    }

    @Test
    @DisplayName("Test Specification queries from saved searches")
    public void testSearchForUpdates() {

        var searchByStore = SearchItemsInStores.newSavedSearches().withStore(storeName);

        // find all updated with an always true search
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.WEIGHT_GRAMS, ">=", "0"));
        assertEquals(6, searchService.search(username).size());

        // find by name
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.STRAIN, "=", "Pyromancy"));
        assertEquals(1, searchService.search(username).size());

        // find by thc
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.THC_PERCENT, ">=", "20"));
        assertEquals(2, searchService.search(username).size());

        // find by no cbd
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.CBD_PERCENT, "=", "0"));
        assertEquals(5, searchService.search(username).size());

        // find by has cbd
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.CBD_PERCENT, ">", "0"));
        assertEquals(1, searchService.search(username).size());

        // find by weight
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.WEIGHT_GRAMS, "=", "3.5"));
        assertEquals(2, searchService.search(username).size());

        // find by indica
        String indica = String.valueOf(Subspecies.INDICA.ordinal());
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.SUBSPECIES, "=", indica));
        assertEquals(2, searchService.search(username).size());

        // find by price
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.PRICE_DOLLARS, "<=", "80"));
        assertEquals(3, searchService.search(username).size());

        // find by vendor
        configService.updateSearchConfig(username, searchByStore.withSearch(StoreItem_.VENDOR, "=", "gLeaf"));
        var results = searchService.search(username);
        results.forEach(item -> assertEquals("gLeaf", item.vendor()));
        assertEquals(1, searchService.search(username).size());
    }

    @Test
    public void testFindByTerpeneAmounts() {

        var searchByStore = SearchItemsInStores.newSavedSearches().withStore(storeName);

        // find by terpene amounts
        ItemSearch terpProfile = new ItemSearch()
                .withParameter(StoreItem_.MYRCENE_PERCENT, ">=", ".1")
                .withParameter(StoreItem_.LINALOOL_PERCENT, ">=", ".05")
                .withParameter(StoreItem_.TERPINOLENE_PERCENT, ">=", ".01")
                .withParameter(StoreItem_.VENDOR, "=", "Rythm");

        SearchItemsInStores sleepyTerappin = searchByStore.withSearch(terpProfile);

        configService.updateSearchConfig(username, sleepyTerappin);

        var results = searchService.search(username);
        results.forEach(item -> assertEquals("Rythm", item.vendor()));
        assertEquals(1, results.size());
    }

}
