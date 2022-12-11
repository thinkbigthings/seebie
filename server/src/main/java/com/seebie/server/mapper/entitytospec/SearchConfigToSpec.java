package com.seebie.server.mapper.entitytospec;

import org.springframework.data.jpa.domain.Specification;
import com.seebie.server.entity.SearchConfig;
import com.seebie.server.entity.Store;
import com.seebie.server.entity.StoreItem;

import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static com.seebie.server.mapper.entitytospec.StoreNamesToSpec.TRUE;

public class SearchConfigToSpec implements Function<SearchConfig, Specification<StoreItem>> {

    private InstantToSpec instantToSpec = new InstantToSpec();
    private StoreNamesToSpec storeNamesToSpec = new StoreNamesToSpec();
    private SearchItemToSpec searchItemToSpec = new SearchItemToSpec();

    /**
     * Within a SavedSearch, each individual search's parameters are AND'ed together.
     * Then at this level, each individual SavedSearch are ORed together,
     * so results of those searches are all added together.
     */
    @Override
    public Specification<StoreItem> apply(SearchConfig config) {

        var sinceLastScan = instantToSpec.apply(config.getLastSearch());

        // if stores list is empty, search across all stores
        var byStore =  config.getSearchStores().stream()
                .map(Store::getName)
                .collect(collectingAndThen(toList(), storeNamesToSpec));

        // if searches are empty, search all items
        var searchItems = config.getSubSearches().stream()
                .map(searchItemToSpec)
                .reduce(Specification::or)
                .orElse(TRUE);

        return searchItems.and(byStore).and(sinceLastScan);
    }

}
