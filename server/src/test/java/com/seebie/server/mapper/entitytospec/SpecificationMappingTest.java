package com.seebie.server.mapper.entitytospec;

import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import com.seebie.server.entity.SavedSearch;
import com.seebie.server.entity.SearchConfig;
import com.seebie.server.entity.Store;
import com.seebie.server.mapper.dtotoentity.SavedSearchRecordToEntity;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpecificationMappingTest {

    private SearchConfigToSpec searchService = new SearchConfigToSpec();

    private SearchConfig config = new SearchConfig();

    private static Set<SavedSearch> userSearchEntities;
    private static Set<Store> storeEntities;

    @BeforeAll
    public static void setup() throws IOException  {
        var userSearches = TestData.readSavedSearch("saved-search-cherry-diesel-or-high-thc.json");
        SavedSearchRecordToEntity dtoToEntity = new SavedSearchRecordToEntity();
        userSearchEntities = userSearches.searches().stream().map(dtoToEntity).collect(toSet());

        storeEntities = new HashSet<>();
        storeEntities.add(new Store("x", "x"));
    }

//    @Test
//    public void testEmptyOnInactive() {
//
//        config.setActive(false);
//        config.setSearchStores(storeEntities);
//        config.setSubSearches(userSearchEntities);
//
//        assertTrue(searchService.apply(config).isEmpty());
//    }
//
//    @Test
//    public void testEmptyOnNoSearches() {
//
//        config.setActive(true);
//        config.setSearchStores(storeEntities);
//        config.setSubSearches(new HashSet<>());
//
//        assertTrue(searchService.apply(config).isEmpty());
//    }
//
//    @Test
//    public void testEmptyOnNoStores() {
//
//        config.setActive(true);
//        config.setSearchStores(new HashSet<>());
//        config.setSubSearches(userSearchEntities);
//
//        assertFalse(searchService.apply(config).isEmpty());
//    }
}
