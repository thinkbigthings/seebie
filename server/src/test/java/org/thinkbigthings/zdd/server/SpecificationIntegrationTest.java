package org.thinkbigthings.zdd.server;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.thinkbigthings.zdd.dto.RegistrationRequest;
import org.thinkbigthings.zdd.dto.ItemSearch;
import org.thinkbigthings.zdd.dto.SearchItemsInStores;
import org.thinkbigthings.zdd.dto.StoreRecord;
import org.thinkbigthings.zdd.server.entity.Subspecies;
import org.thinkbigthings.zdd.server.service.*;
import org.thinkbigthings.zdd.server.test.data.TestData;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.thinkbigthings.zdd.server.entity.StoreItem_.*;
import static org.thinkbigthings.zdd.server.test.data.TestData.createRandomUserRegistration;
import static org.thinkbigthings.zdd.server.test.data.TestData.readItems;


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
        storeService.updateStoreItems(storeName, readItems("keystone-devon-flower-20210909.json"));

        RegistrationRequest testUserRegistration = createRandomUserRegistration();
        userService.saveNewUser(testUserRegistration);
        username = testUserRegistration.username();

        // update again, then last scan will grab items changed since the first load
        storeService.updateStoreItems(storeName, readItems("keystone-devon-flower-20210910.json"));
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
        configService.updateSearchConfig(username, searchByStore.withSearch(WEIGHT_GRAMS, ">=", "0"));
        assertEquals(6, searchService.search(username).size());

        // find by name
        configService.updateSearchConfig(username, searchByStore.withSearch(STRAIN, "=", "Pyromancy"));
        assertEquals(1, searchService.search(username).size());

        // find by thc
        configService.updateSearchConfig(username, searchByStore.withSearch(THC_PERCENT, ">=", "20"));
        assertEquals(2, searchService.search(username).size());

        // find by no cbd
        configService.updateSearchConfig(username, searchByStore.withSearch(CBD_PERCENT, "=", "0"));
        assertEquals(5, searchService.search(username).size());

        // find by has cbd
        configService.updateSearchConfig(username, searchByStore.withSearch(CBD_PERCENT, ">", "0"));
        assertEquals(1, searchService.search(username).size());

        // find by weight
        configService.updateSearchConfig(username, searchByStore.withSearch(WEIGHT_GRAMS, "=", "3.5"));
        assertEquals(2, searchService.search(username).size());

        // find by indica
        String indica = String.valueOf(Subspecies.INDICA.ordinal());
        configService.updateSearchConfig(username, searchByStore.withSearch(SUBSPECIES, "=", indica));
        assertEquals(2, searchService.search(username).size());

        // find by price
        configService.updateSearchConfig(username, searchByStore.withSearch(PRICE_DOLLARS, "<=", "80"));
        assertEquals(3, searchService.search(username).size());

        // find by vendor
        configService.updateSearchConfig(username, searchByStore.withSearch(VENDOR, "=", "gLeaf"));
        var results = searchService.search(username);
        results.forEach(item -> assertEquals("gLeaf", item.vendor()));
        assertEquals(1, searchService.search(username).size());
    }

    @Test
    public void testFindByTerpeneAmounts() {

        var searchByStore = SearchItemsInStores.newSavedSearches().withStore(storeName);

        // find by terpene amounts
        ItemSearch terpProfile = new ItemSearch()
                .withParameter(MYRCENE_PERCENT, ">=", ".1")
                .withParameter(LINALOOL_PERCENT, ">=", ".05")
                .withParameter(TERPINOLENE_PERCENT, ">=", ".01")
                .withParameter(VENDOR, "=", "Rythm");

        SearchItemsInStores sleepyTerappin = searchByStore.withSearch(terpProfile);

        configService.updateSearchConfig(username, sleepyTerappin);

        var results = searchService.search(username);
        results.forEach(item -> assertEquals("Rythm", item.vendor()));
        assertEquals(1, results.size());
    }

}
